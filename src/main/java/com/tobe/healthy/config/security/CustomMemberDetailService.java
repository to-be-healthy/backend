package com.tobe.healthy.config.security;

import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		return memberRepository.findById(Long.valueOf(userId))
			.map(CustomMemberDetails::new)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
	}
}
