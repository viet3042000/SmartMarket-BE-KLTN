//package com.example.authserver.config;
//
//import org.json.JSONObject;
//import org.springframework.http.HttpHeaders;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import org.springframework.http.ResponseCookie;
//
//@Component
//public class CustomAddCookiesFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//        ResponseWrapper responseCopier = new ResponseWrapper(response);
//        chain.doFilter(request, responseCopier);
//
////        get response body after doFilter
//        byte[] body = responseCopier.getCopy();
//        String stringBody = new String(body, response.getCharacterEncoding());
//        JSONObject responseBody = new JSONObject(stringBody);
//        if(!responseBody.isNull("access_token")) {
////            Cookie cookie = new Cookie("accessToken", responseBody.getString("access_token"));
////
//////            cookies are only accessible from a server
////            cookie.setHttpOnly(true);
////
//////            cookie must be transmitted over HTTPS
//////            cookie.setSecure(true); //postman doesn't auto add --> header
////
//////            cookie.setDomain("");// auto = localhost
////            cookie.setDomain("http://10.14.101.202:31441/");
//////            cookie.setPath("/");
////            cookie.setMaxAge(24 * 60 * 60); //1 days
//
//            ResponseCookie cookie = ResponseCookie.from("accessToken",responseBody.getString("access_token"))
//                    .httpOnly (true)
//                    .secure (false)
//                    .domain ("localhost")
//                    .path("/")
//                    .maxAge (24 * 60 * 60)
//                    .sameSite ("Strict")
//                    .build();
//
////            response.addCookie(cookie);
//            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//        }
//    }
//}
