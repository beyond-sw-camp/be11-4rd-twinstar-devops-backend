package com.TwinStar.TwinStar.user.domain;

import com.TwinStar.TwinStar.common.domain.BaseTimeEntity;
import com.TwinStar.TwinStar.common.domain.Visibility;
import com.TwinStar.TwinStar.common.domain.YN;
import com.TwinStar.TwinStar.post.domain.Post;
import com.TwinStar.TwinStar.user.dto.ChatUserListDto;
import com.TwinStar.TwinStar.user.dto.UserListDto;
import com.TwinStar.TwinStar.user.dto.UserProfileDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Transactional
@Builder
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true, name = "email")
    private String email;
    @Column(nullable = false, unique = true)
    private String nickName;
    @Builder.Default
    private String profileImg = "/images/default_profile.png"; //기본 프로필 이미지 설정
    private String profileTxt;
    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private YN delYn = YN.valueOf("N");
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Sex sex;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility idVisibility = Visibility.ALL;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AdminYn adminYn = AdminYn.USER;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;
    private LocalDateTime banCloseTime; // 정지 해제 날짜
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)//자동저장/ 삭제는 메소드 사용
    @Builder.Default //회원가입하면 게시물이 0개
    private List<Post> posts = new ArrayList<>();//ProfilePostResDto를 가져올 수 없음.post를 가져와야함
    
//    following, follower, reports 필드는 각 레파지토리에서 가져오는 것이 성능적으로 더 나은 것 같아서 삭제함

    //    관리자용 유저 목록조회
    public UserListDto listFromEntity() {
        return UserListDto.builder()
                .id(this.id)
                .email(this.email)
                .nickName(this.nickName)
                .sex(this.sex)
                .idVisibility(this.idVisibility)
                .userStatus(this.userStatus)
                .adminYn(this.adminYn)
                .delYn(this.delYn)
                .createdAt(this.getCreatedTime())
                .build();
    }

    //    사용자 프로필 업데이트
    public void updateProfile(String nickName, String profileTxt, Visibility idVisibility,Sex sex) {
        this.nickName = nickName;
        this.profileTxt = profileTxt;
        this.idVisibility = idVisibility;
        this.sex = sex;
    }

    //    프로필 이미지 변경
    public void updateProfileImage(String profileImgUrl) {
        this.profileImg = profileImgUrl;
    }

    //소프트 딜리트메서드 추가
    public void deleteUser() {
        this.delYn = YN.valueOf("Y");
    }

    //    비밀번호 변경
    public void changePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }


    //    상태 변경을 위한 메서드
    public void changeStatus(Visibility newIdVisibility){
        if (this.idVisibility == newIdVisibility){
            throw new IllegalStateException("이미 현재상태와 동일합니다.");
        }
        this.idVisibility = newIdVisibility;
    }

    // 관리자 권한 변경 메서드
    public void changeAdmin(AdminYn newRole){
        this.adminYn = newRole;
    }

    //  계정 정지 메서드 추가
    public void ban(Integer banCloseTime) {
        this.userStatus = UserStatus.BAN;
//        banCloseTime이 null값이면 무기한 정지
        this.banCloseTime = (banCloseTime == null) ? null : LocalDateTime.now().plusDays(banCloseTime);
    }

    // 계정 정지 해제 메서드 추가
    public void unban() {
        this.userStatus = UserStatus.ACTIVE;
        this.banCloseTime = null;//너는 null하면 안되는데
    }

    public boolean isBanned() {
        // 무기한 정지이거나, banCloseTime이 현재 시간보다 이후면 로그인 차단
        return this.userStatus == UserStatus.BAN && (banCloseTime == null || banCloseTime.isAfter(LocalDateTime.now()));
    }

}
