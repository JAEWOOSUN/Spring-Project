package solis.pl.service.loginTest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import solis.pl.domain.apiTest.GoogleOAuthRequest;
import solis.pl.domain.apiTest.GoogleOAuthResponse;
import solis.pl.domain.loginTest.loginTestUserDetails;
import solis.pl.repository.LoginTestMapper;
import solis.pl.service.user.CustomPasswordEncoder;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LoginTestService  {

    @Inject
    LoginTestMapper loginTestMapper;

    @Inject
    private CustomPasswordEncoder passwordEncoder;

    private static AuthenticationManager am = new SampleAuthenticationManager();

    public Map<String, String>  googleRedirectService(String googleAPIkey, String googleAPIsecret, String code) throws IOException {

        Map<String, String> userInfo;

        try{
            //참조 : https://gdtbgl93.tistory.com/182
            //RestTemplate 설명 : https://sjh836.tistory.com/141
            RestTemplate restTemplate = new RestTemplate();

            GoogleOAuthRequest googleOAuthRequestParam = new GoogleOAuthRequest();
            googleOAuthRequestParam.setClientId(googleAPIkey);
            googleOAuthRequestParam.setClientSecret(googleAPIsecret);
            googleOAuthRequestParam.setCode(code);
            googleOAuthRequestParam.setRedirectUri("http://localhost:8080/loginTest/google-redirect");
            googleOAuthRequestParam.setGrantType("authorization_code");

            //JWT TOKEN
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            ResponseEntity<String> resultEntity = restTemplate.postForEntity("https://accounts.google.com/o/oauth2/token", googleOAuthRequestParam, String.class);
            GoogleOAuthResponse result = mapper.readValue(resultEntity.getBody(), new TypeReference<GoogleOAuthResponse>() {});
            String jwtToken = result.getIdToken();
            System.out.println("jwt token : "+jwtToken);

            String requestUrl = UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/tokeninfo")
                    .queryParam("id_token", jwtToken).toUriString();

            String resultJson = restTemplate.getForObject(requestUrl, String.class);

            userInfo = mapper.readValue(resultJson, new TypeReference<Map<String, String>>() {});

            //Insert userInfo into loginTestUserDetails
            insertUserInfoINTOSecurityContextHolder(userInfo);

            return userInfo;

        } catch(Exception e){
            e.printStackTrace();
            userInfo = null;

            return userInfo;

        }

    }

    @Transactional
    public void insertUserInfoINTOSecurityContextHolder(Map<String, String> userInfo){

        loginTestUserDetails userTemp = new loginTestUserDetails();

        userTemp.setID(userInfo.get("email"));
        userTemp.setNAME(userInfo.get("email"));
        userTemp.setPW(userInfo.get("sub"));
        userTemp.setAUTHORITY("ROLE_USER");

        Authentication requestAUTH = new UsernamePasswordAuthenticationToken(userTemp, null);
        Authentication resultAUTH = am.authenticate(requestAUTH);

        //Save Google jwt information in j_spring_security authentication context
        SecurityContextHolder.getContext().setAuthentication(resultAUTH);

    }
}

class SampleAuthenticationManager implements AuthenticationManager{
    static final List AUTHORITIES = new ArrayList();

    static {
        AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        if(!auth.getName().equals(auth.getCredentials())){
            return new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), AUTHORITIES);
        }
        throw new BadCredentialsException("Bad Credentials");
    }
}


