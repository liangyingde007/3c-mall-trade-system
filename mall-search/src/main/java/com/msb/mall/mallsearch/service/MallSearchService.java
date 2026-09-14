package com.msb.mall.mallsearch.service;

import com.msb.mall.mallsearch.vo.SearchParam;
import com.msb.mall.mallsearch.vo.SearchResult;

public interface MallSearchService {

    SearchResult search(SearchParam param);
}
