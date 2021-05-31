package com.smartmarket.code.websercurityservice;

import com.smartmarket.code.dao.ClientRepository;
import com.smartmarket.code.dao.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class WebSecurity {

    @Autowired
    ClientRepository clientRepository ;

    @Autowired
    UrlRepository urlRepository ;


    public boolean check(Authentication authentication) throws IOException {

        HttpServletRequest request = (HttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();


//        String responseError = new String("FAILED");
//
//        ResponseEntity.ok(responseError) ;
//        entity.sendError(HttpStatus.BAD_REQUEST.value(),"can tao them");
//        String url = request.getRequestURI() ;
//        Map<String, Object> claims = null;
//
//        claims = JwtUtils.getClaimsMap(authentication) ;
//        String userName = (String) claims.get("user_name");

        return false ;
    }

//    public boolean checkURL(Authentication authentication) throws CustomException {
//
//        //declare
//        String url =  "";
//        Map<String, Object> claims = null;
//        Set<Url> urlSet = null ;
//        claims = JwtUtils.getClaimsMap(authentication) ;
//        String clientId = null;
//
//        if (claims != null){
//            clientId = (String) claims.get("client_id");
//        }else {
//            throw new CustomException("Không tìm thấy client ID trong token") ;
//        }
//
//        //find user by name
//        Client client =
//                clientRepository.findByclientName(clientId).orElseThrow(
//                        () -> new CustomException("Không tìm thấy Client trong danh sách với clientId")
//                );
//
//        urlSet = urlRepository.findUrlByClientIdActive(client.getId()) ;
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String currentUrlRequest = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
////        Map<String, Object> map =
////                objectMapper.convertValue(ServletUriComponentsBuilder.fromCurrentRequest(), Map.class);
////        ServletUriComponentsBuilder.fromCurrentRequestUri();
////        Map<String, Object> claims = null;
////        claims = JwtUtils.getClaimsMap(authentication) ;
////        String customField = (String) claims.get("user_name");
//
//        return true ;
//    }
}
