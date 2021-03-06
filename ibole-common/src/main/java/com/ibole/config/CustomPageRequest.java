/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibole.config;

import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;

/**
 * 自定义page实现 page -1
 * Basic Java Bean implementation of {@link Pageable} with support for QueryDSL.
 *
 * @author Thomas Darimont
 */
public class CustomPageRequest extends AbstractPageRequest {

	private static final long serialVersionUID = 7529171950267879273L;

	private final QSort sort;

	/**
	 * Creates a new {@link CustomPageRequest}. Pages are zero indexed, thus providing 0 for {@code page} will return the first
	 * page.
	 *
	 * @param page
	 * @param size
	 */
	public CustomPageRequest(int page, int size) {
		this(page != 0 ? page - 1 : 0, size, QSort.unsorted());
	}

	/**
	 * Creates a new {@link CustomPageRequest} with the given {@link OrderSpecifier}s applied.
	 *
	 * @param page
	 * @param size
	 * @param orderSpecifiers must not be {@literal null} or empty;
	 */
	public CustomPageRequest(int page, int size, OrderSpecifier<?>... orderSpecifiers) {
		this(page, size, new QSort(orderSpecifiers));
	}

	/**
	 * Creates a new {@link CustomPageRequest} with sort parameters applied.
	 *
	 * @param page
	 * @param size
	 * @param sort
	 */
	public CustomPageRequest(int page, int size, QSort sort) {
		super(page, size);

		this.sort = sort;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#getSort()
	 */
	@Override
	public Sort getSort() {
		return sort;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.AbstractPageRequest#next()
	 */
	@Override
	public Pageable next() {
		return new CustomPageRequest(getPageNumber() + 1, getPageSize(), sort);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.AbstractPageRequest#previous()
	 */
	@Override
	public Pageable previous() {
		return new CustomPageRequest(getPageNumber() - 1, getPageSize(), sort);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.AbstractPageRequest#first()
	 */
	@Override
	public Pageable first() {
		return new CustomPageRequest(0, getPageSize(), sort);
	}
}
