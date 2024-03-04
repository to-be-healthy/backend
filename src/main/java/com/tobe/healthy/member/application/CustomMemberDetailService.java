package com.tobe.healthy.member.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomMemberDetailService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		List<GrantedAuthority> roles = new ArrayList<>();
		roles.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
		return new AccountContext(email, roles);
	}

	public static class AccountContext extends User {

		public AccountContext(String username, Collection<? extends GrantedAuthority> authorities) {
			super(username, "", authorities);
		}
	}
}
