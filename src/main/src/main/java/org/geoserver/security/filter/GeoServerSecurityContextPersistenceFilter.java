/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.geoserver.security.config.SecurityContextPersistenceFilterConfig;
import org.geoserver.security.config.SecurityNamedServiceConfig;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

/**
 * Security context persitence filter 
 * 
 * @author mcr
 *
 */
public class GeoServerSecurityContextPersistenceFilter extends GeoServerCompositeFilter {
    
    public final static String ALLOWSESSIONCREATION_ATTR = "_allowSessionCreation"; 
    Boolean isAllowSessionCreation;
    
    @Override
    public void initializeFromConfig(SecurityNamedServiceConfig config) throws IOException {
        super.initializeFromConfig(config);
        
        
        SecurityContextPersistenceFilterConfig pConfig = 
                (SecurityContextPersistenceFilterConfig) config;
                
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        SecurityContextPersistenceFilter filter = new SecurityContextPersistenceFilter(repo) {
          @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                throws IOException, ServletException {
                 // set the hint for authentcation servlets
                 req.setAttribute(ALLOWSESSIONCREATION_ATTR, isAllowSessionCreation);
            super.doFilter(req, res, chain);
        }  
        };
        isAllowSessionCreation=pConfig.isAllowSessionCreation();
        repo.setAllowSessionCreation(pConfig.isAllowSessionCreation());        
        filter.setForceEagerSessionCreation(false);

        try {
            filter.afterPropertiesSet();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        getNestedFilters().add(filter);        
    }
    
}
