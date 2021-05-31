//package com.smartmarket.code.config;
//
//import com.smartmarket.code.dao.ClientRepository;
//import com.smartmarket.code.dao.UrlRepository;
//import com.smartmarket.code.exception.CustomException;
//import com.smartmarket.code.model.Client;
//import com.smartmarket.code.model.Url;
//import com.smartmarket.code.util.JwtUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//
//@Component
//public class CustomAuthorizeRequestFilter extends OncePerRequestFilter {
//
//    @Autowired
//    ClientRepository clientRepository;
//
//    @Autowired
//    UrlRepository urlRepository;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
//        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
//        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
//        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");
//
//        try {
//
//            String cururl = request.getRequestURI().toLowerCase();
//            String contextPath = request.getContextPath().toLowerCase();
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            //declare
//            String url = "";
//            Map<String, Object> claims = null;
//            Set<Url> urlSet = null;
//            claims = JwtUtils.getClaimsMap(authentication);
//            String clientId = null;
//
//            //get clientid from claims
//            if (claims != null) {
//                clientId = (String) claims.get("client_id");
//            } else {
//                httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value(), "Not found client id in token");
//                return;
//            }
//
//            //find client by clientName
//            Optional<Client> client = clientRepository.findByclientName(clientId);
//            if (client.isPresent() == true) {
//                urlSet = urlRepository.findUrlByClientIdActive(client.get().getId());
//
//                //check url access
//                if (urlSet == null) {
//                    httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value(), "Not found url access for client id");
//                    return;
//                }
//
//            } else {
//                httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value(), "Not found client id ");
//                return;
//            }
//
//
//            AntPathMatcher matcher = new AntPathMatcher();
//            String method = request.getMethod();
//
//            // verify endpoint
//            String URLRequest = request.getRequestURI();
//            boolean verifyEndpoint = false;
//
//            if (client.isPresent() == true && urlSet != null) {
//                for (Url url1 : urlSet) {
//
//                    String path = url1.getPath();
//                    if (matcher.match(path, URLRequest)) {
//                        verifyEndpoint = true;
//                        break;
//
//                    }
//                }
//            }
//
//            if (verifyEndpoint == false) {
//                httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Client id is not authorized");
//                return;
//            }
//
//        } catch (CustomException ex) {
//            httpServletResponse.sendError(ex.getHttpStatus().value(), ex.getMessage());
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//}