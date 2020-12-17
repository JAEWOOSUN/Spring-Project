package solis.pl.domain.loginTest;

import org.apache.http.auth.AUTH;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import scala.xml.Null;
import solis.pl.domain.constant.Role;
import solis.pl.domain.user.Authority;
import solis.pl.service.user.CustomPasswordEncoder;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public class loginTestUserDetails implements UserDetails {

    private String ID;
    private String PW;
    private String AUTHORITY;
    private boolean ENABLED;
    private String NAME;

    @Inject
    private CustomPasswordEncoder passwordEncoder;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
        auth.add(new SimpleGrantedAuthority(AUTHORITY));
        return auth;
    }

    public void setID(String ID) { this.ID = ID; }

    public void setPW(String PW) { this.PW = PW; }

    @Override
    public String getPassword() {
        return PW;
    }

    @Override
    public String getUsername() {
        return ID;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ENABLED;
    }

    public String getNAME(){
        return NAME;
    }

    public void setNAME(String name){
        this.NAME = name;
    }

    //현재 권한에 대해 출력
    public static loginTestUserDetails current(){
        try{
            return (loginTestUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (NullPointerException | ClassCastException e){
            return null;
        }
    }

    public boolean hasRole(Role role){
        if(AUTHORITY == role.toCode())
            return true;
        else
            return false;
    }

    public String getAUTHORITY() {
        return AUTHORITY;
    }

    public void setAUTHORITY(String AUTHORITY) {
        this.AUTHORITY = AUTHORITY;
    }

    public boolean matches(String pw){
        return passwordEncoder.matches(pw, this.PW);
    }
}
