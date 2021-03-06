package com.ibole.user.service;

import com.ibole.api.base.LoginLogServiceRpc;
import com.ibole.db.redis.service.RedisService;
import com.ibole.exception.custom.ResourceNotFoundException;
import com.ibole.exception.custom.UserException;
import com.ibole.pojo.QueryVO;
import com.ibole.pojo.user.QUser;
import com.ibole.pojo.user.Role;
import com.ibole.pojo.user.User;
import com.ibole.pojo.user.UserRole;
import com.ibole.user.dao.ResourceDao;
import com.ibole.user.dao.RoleDao;
import com.ibole.user.dao.UserDao;
import com.ibole.user.dao.UserRoleDao;
import com.ibole.utils.DateUtil;
import com.ibole.utils.QuerydslUtil;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务
 **/
@Service
public class UserService {

	private final UserDao userDao;
	private final RoleDao roleDao;
    private final ResourceDao resourceDao;

    private final RedisService redisService;
    // 加密
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final LoginLogServiceRpc loginLogServiceRpc;

    private final UserRoleDao userRoleDao;

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    public UserService(UserDao userDao, RedisService redisService,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       LoginLogServiceRpc loginLogServiceRpc, UserRoleDao userRoleDao,
                       RoleDao roleDao, ResourceDao resourceDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.resourceDao = resourceDao;
        this.redisService = redisService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.loginLogServiceRpc = loginLogServiceRpc;
		this.userRoleDao = userRoleDao;
	}

	/**
	 * 注册用户
	 *
	 * @param user
	 */
    public void registerUser(User user) {
        user.setCreateAt(DateUtil.getTimestamp());
        user.setUpdateAt(DateUtil.getTimestamp());
        //加密后的密码
        String bCryptPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(bCryptPassword);
        userDao.save(user);
    }


    public QueryResults<User> findByCondition(User user, QueryVO queryVO) {

        QUser qUser = QUser.user;
        com.querydsl.core.types.Predicate predicate = null;
        OrderSpecifier<?> sortedColumn = QuerydslUtil.getSortedColumn(Order.DESC, qUser);
        if (StringUtils.isNotEmpty(user.getUserName())) {
            predicate = ExpressionUtils.and(predicate, qUser.userName.like(user.getUserName()));
        }
        if (user.getStatus() != null) {
            predicate = ExpressionUtils.and(predicate, qUser.status.eq(user.getStatus()));
        }
        if (StringUtils.isNotEmpty(user.getId())) {
            predicate = ExpressionUtils.and(predicate, qUser.status.eq(user.getStatus()));
        }
        if (StringUtils.isNotEmpty(user.getUserName())) {
            predicate = ExpressionUtils.and(predicate, qUser.status.eq(user.getStatus()));
        }
        if (StringUtils.isNotEmpty(user.getAccount())) {
            predicate = ExpressionUtils.and(predicate, qUser.account.eq(user.getAccount()));
        }
        if (StringUtils.isNotEmpty(user.getPhone())) {
            predicate = ExpressionUtils.and(predicate, qUser.phone.eq(user.getPhone()));
        }
        if (StringUtils.isNotEmpty(queryVO.getFieldSort())) {
            sortedColumn = QuerydslUtil.getSortedColumn(Order.DESC, qUser, queryVO.getFieldSort());
        }
        QueryResults<User> queryResults = jpaQueryFactory
                .selectFrom(qUser)
                .where(predicate)
                .offset(queryVO.getPageNum())
                .limit(queryVO.getPageSize())
                .orderBy(sortedColumn)
                .fetchResults();
        queryResults.getResults().forEach(
                userResult -> userResult.setRoles(roleDao.findRolesOfUser(userResult.getId()))
        );
        return queryResults;
    }

    public void deleteByIds(List<String> userId) {
        userDao.deleteBatch(userId);
    }

    /**
	 * 更新用户基础信息，关联的角色
	 *
	 * @param user 用户实体
	 * @return boolean
	 */
	public void updateByPrimaryKey(User user) {
		userDao.save(user);
		userRoleDao.deleteBytUsUserId(user.getId());
		List<Role> roles = user.getRoles();
		for (Role role : roles) {
			UserRole userRole = new UserRole();
			userRole.setUsUserId(user.getId());
			userRole.setUsRoleId(role.getId());
			userRoleDao.save(userRole);
		}
	}


	/**
	 * 修改密码
	 *
	 * @param user        当前用户
	 * @param oldPassword 老密码
	 */
	public void changePassword(User user, String oldPassword) {
		User userInfo = userDao.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("用户不存在！"));
		if (!bCryptPasswordEncoder.matches(oldPassword, userInfo.getPassword())) {
			throw new UserException("密码不匹配！");
		}
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userDao.save(user);
	}

	/**
	 * 获取用户角色权限
	 * @param id 用户id
	 */
	public User getUserPermission(String id) {
		User user = userDao.findById(id).orElseThrow(ResourceNotFoundException::new);
		user.setRoles(roleDao.findRolesOfUser(id));
		user.setResource(resourceDao.findResourcesOfUser(id));
		return user;
	}

	public List<Role> getUseRoles(String id) {
		List<Role> rolesOfUser = roleDao.findRolesOfUser(id);
		return rolesOfUser;
	}


	public User findById(String userId) {
		User user = userDao.findById(userId).orElseThrow(ResourceNotFoundException::new);
		user.setRoles(roleDao.findRolesOfUser(user.getId()));
		return user;
	}

	public void updateUserProfile(User user) {
		userDao.save(user);
	}
}
