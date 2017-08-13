package com.katsura.controller.filter;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.config.CacheConfiguration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(CacheConfiguration.class)
public class PageCacheConfiguration {

    @Bean
    public FilterRegistrationBean registerBlogsPageFilter(){
        CustomPageCachingFilter customPageCachingFilter = new CustomPageCachingFilter("com.katsura.Songs");
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(customPageCachingFilter);
        List<String> urls = new ArrayList<String>();
        urls.add("/songs");
        filterRegistrationBean.setUrlPatterns(urls);
        return filterRegistrationBean;
    }

}