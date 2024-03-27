package org.pageflow.boundedcontext.user.constants;

import java.util.Set;

/**
 * @author : sechan
 */
public class UserSignupPolicy {
    
    public static final Set<String> FORBIDDEN_USERNAME_WORDS =
            Set.of(
                    "admin","administrator","anonymous","pageflow"
            );
    
    public static final Set<String> FORBIDDEN_PENNAME_WORDS =
            Set.of(
                    "admin","administrator","anonymous","pageflow","관리자","어드민","페이지플로우","패이지플로우","매니저",
                    "패플","도우미","고객센터","고객센타","고객샌터","고객샌타","한게임","한개임","운영자","엔토이","앤토이","씨펄",
                    "메니저","매니져","메니져","스탭","스텝","갈보","강간","개년","개놈","개뇬","개보지","개삽년","개새끼","개세이",
                    "개쉐이","개자식","개자지","개지랄","꼬추","노브라","니미","니미랄","니미럴","니애미","니에미","등신","씨발뇬","포르노",
                    "딸딸이","또라이","레즈비언","멜섹","몰카","문섹","미친넘","미친년","미친놈","미친뇬","번색","번섹","번쌕","병신","미친",
                    "보지","보댕이","부랄","부부교환","불알","빙신","빠구리","빠굴","빠꾸리","빡우리","빡울","뽀르노","새꺄","새끈",
                    "새끈남","새끈녀","새끼","색남","색녀","색스","색폰","성인만화","성인물","성인소설","성인엽기","성인영화","성인용",
                    "성인용품","성인잡지","세꺄","섹녀","섹스","쉬팔","쉬펄","스발","시발","시벌","시파","시펄","십팔금","쌍넘","쌍년",
                    "쌍놈","쌔깐","쌔끈","쌕쓰","쌕폰","썅","쒸팔","쒸펄","쓰바","씌팍","씨바","씨발","씨발넘","씨발년","씨발놈","젖탱",
                    "씨방","씨방새","씨버럴","씨벌","씨보랄","씨보럴","씨부랄","씨부럴","씨부리","씨불","씨브랄","씨파","씨팍","씨팔",
                    "씹","야동","야사","야설","야캠","야한","에로","오랄","와레즈","원조교재","원조교제","음란","자위","자지","젖꼭지",
                    "젖탱이","젼나","조까","졸라","좃","좆","지랄","지미랄","창녀","창년","창놈","창뇬","캠색","캠섹","페니스","페티쉬",
                    "폰색","폰쌕","헨타이","호로새끼","호빠","호스테스바","호스트바","화상색","화상섹","자살","음독","청산가리","청산가루",
                    "븅신", "븅딱"
            );
    
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final int USERNAME_MAX_LENGTH = 100;
    public static final String USERNAME_REGEX_DISCRIPTION = "아이디는 영문, 숫자만으로 이루어진 "+USERNAME_MIN_LENGTH+"~"+USERNAME_MAX_LENGTH+"자여야 합니다";
    // 대소문자, 숫자, - 문자만 허용
    public static final String USERNAME_REGEX = "^[-a-zA-Z0-9]{"+USERNAME_MIN_LENGTH+","+USERNAME_MAX_LENGTH+"}$";
    
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 36;
    public static final String PASSWORD_REGEX_DISCRIPTION = "비밀번호는 영문, 숫자를 포함한 "+PASSWORD_MIN_LENGTH+"~"+PASSWORD_MAX_LENGTH+"자여야 합니다";
    // 대소문자, 숫자, 일부 특수문자만 허용
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])([A-Za-z0-9~!@#$%^&*()+_|=]|[-]){"+PASSWORD_MIN_LENGTH+","+PASSWORD_MAX_LENGTH+"}$";
    
    public static final int PENNAME_MIN_LENGTH = 1;
    public static final int PENNAME_MAX_LENGTH = 12;
    public static final String PENNAME_REGEX_DISCRIPTION = "필명은 한글, 영문, 숫자로 이루어진 "+PENNAME_MIN_LENGTH+"~"+PENNAME_MAX_LENGTH+"자여야 합니다";
    // 한글, 영문, 숫자만 허용
    public static final String PENNAME_REGEX = "^[가-힣a-zA-Z0-9]{"+PENNAME_MIN_LENGTH+","+PENNAME_MAX_LENGTH+"}$";
    
}