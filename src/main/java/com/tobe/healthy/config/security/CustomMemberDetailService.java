package com.tobe.healthy.config.security;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		return memberRepository.findByUserId(userId)
			.map(CustomMemberDetails::new)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
	}
}
