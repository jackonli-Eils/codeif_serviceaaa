package com.ibole.auth.validate.impl.captcha;

import com.ibole.auth.validate.impl.ValidateCode;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;


/**
 * 图片验证码
 * @author zhailiang
 *
 */
public class Captcha extends ValidateCode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6020470039852318468L;
	
	private BufferedImage image; 
	
	public Captcha(BufferedImage image, String code, int expireIn){
		super(code, expireIn);
		this.image = image;
	}
	
	public Captcha(BufferedImage image, String code, LocalDateTime expireTime){
		super(code, expireTime);
		this.image = image;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

}
