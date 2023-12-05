package org.pageflow.base.dev;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.model.request.ChapterUpdateRequest;
import org.pageflow.domain.book.model.request.PageUpdateRequest;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.book.service.BookWriteService;
import org.pageflow.domain.interaction.entity.Comment;
import org.pageflow.domain.interaction.model.InteractionPair;
import org.pageflow.domain.interaction.service.CommentService;
import org.pageflow.domain.interaction.service.PreferenceService;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.model.dto.AdditionalSignupAccountDto;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Configuration
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!prod")
@Slf4j
public class DummyDataCreator {

    private final BookService bookService;
    private final AccountService accountService;
    private final BookWriteService bookWriteService;
    private final PreferenceService preferenceService;
    private final CommentService commentService;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;
    
    private int userN = 100;
    private int bookN = 100;
    private int maxCommentN = 10;
    private int maxBookPreferenceN = 10; // 실제 최대 개수는 maxBookPreferenceN를 넘지않는 최대 정수
    private int maxCommentPreferenceN = 10; // 실제 최대 개수는 maxCommentPreferenceN를 넘지않는 최대 정수

    @Bean
    public ApplicationRunner init() {
        return args -> {
            if(ddlAuto.equals("create")){
                createDummyData();
            }
        };
    };
    
    @Transactional
    private void createDummyData(){
        createAccounts(userN);
        createBooks(bookN);
    }
    
    
    
    
    
    
    
    private void createAccounts(Integer n){
        // 사용자 생성
        for(int i = 0; i < n; i++){
            AdditionalSignupAccountDto user = AdditionalSignupAccountDto.builder()
                    .provider(ProviderType.NATIVE)
                    .username("user" + (i + 1))
                    .password("user" + (i + 1))
                    .nickname(getRandomNickName())
                    .email(getRandomId() + i + "@pageflow.com")
                    .profileImgUrl(getRandomProfileImgUrl())
                    .build();
            accountService.register(user);
        }
    }
    
    private void createBooks(Integer bookN){
        
        for(int i = 0; i < bookN; i++){
            Book book = bookWriteService.createBlankBook(profileRepository.findById(randomLDownN(userN)).orElseThrow());
            book.setCoverImgUrl(getRamdomCoverImgUrl());
            book.setTitle("책 제목 " + (i + 1));
            bookService.repoSaveBook(book);
            
            Random random = new Random();
            
            // 선호 생성
            Set<Long> userIds = new HashSet<>(); // 이미 선호를 누른 사용자 집합
            int currentMaxBookPreferenceN = random.nextInt(maxBookPreferenceN);
            for(int j = 0; j < currentMaxBookPreferenceN; j++){
                Long userId;
                while(true){
                    userId = randomLDownN(userN);
                    if(!userIds.contains(userId)){
                        userIds.add(userId);
                        break;
                    }
                }
                
                Profile user = profileRepository.findById(userId).orElseThrow();
                InteractionPair<Book> pair = new InteractionPair<>(user, book);
                preferenceService.createPreference(pair, new Random().nextBoolean());
            }
            
            // 댓글 생성
            int currentMaxCommentN = random.nextInt(maxCommentN);
            for(int k = 0; k < currentMaxCommentN; k++){
                
                Profile user = profileRepository.findById(randomLDownN(userN)).orElseThrow();
                InteractionPair<Book> pair = new InteractionPair<>(user, book);
                Comment comment = commentService.createComment(pair, getRandomComment()); // 댓글 생성
                
                // 댓글 선호 생성
                Set<Long> commentUserIds = new HashSet<>(); // 이미 선호를 누른 사용자 집합
                for(int j = 0; j < random.nextInt(maxCommentPreferenceN); j++){
                    Long userId;
                    while(true){
                        userId = randomLDownN(userN);
                        if(!commentUserIds.contains(userId)){
                            commentUserIds.add(userId);
                            break;
                        }
                    }
                    
                    InteractionPair<Comment> commentPair = new InteractionPair<>(profileRepository.findById(userId).orElseThrow(), comment);
                    preferenceService.createPreference(commentPair, new Random().nextBoolean());
                }
                
            }
            
            Chapter defaultChapter = book.getChapters().get(0);
            defaultChapter.setTitle(getRandomTitle());
            bookWriteService.updateChapter(
                    new ChapterUpdateRequest(defaultChapter)
            );
            Page defaultPage = defaultChapter.getPages().get(0);
            defaultPage.setTitle(getRandomTitle());
            defaultPage.setContent(getRandomContent());
            bookWriteService.updatePage(
                    new PageUpdateRequest(defaultPage)
            );
            
            for(int j = 0; j < randomIDownN(8)+2; j++){
                Chapter chapter = bookWriteService.createBlankChapter(book);
                chapter.setTitle(getRandomTitle());
                bookWriteService.updateChapter(
                        new ChapterUpdateRequest(chapter)
                );
                
                Page blackPage = chapter.getPages().get(0);
                blackPage.setTitle(getRandomTitle());
                blackPage.setContent(getRandomContent());
                bookWriteService.updatePage(
                        new PageUpdateRequest(blackPage)
                );
                
                for(int k = 0; k < randomIDownN(8)+2; k++){
                    Page page = bookWriteService.createBlankPage(chapter);
                    page.setTitle(getRandomTitle());
                    page.setContent(getRandomContent());
                    bookWriteService.updatePage(
                            new PageUpdateRequest(page)
                    );
                }
            }
        }
    }
    
    public String getRandomNickName() {
        List<String> nick = Arrays.asList("기분나쁜","기분좋은","신바람나는","상쾌한","짜릿한","그리운","자유로운","서운한","울적한","비참한","위축되는","긴장되는","두려운","당당한","배부른","수줍은","창피한","멋있는",
                "열받은","심심한","잘생긴","이쁜","시끄러운");
        List<String> name = Arrays.asList("사자","코끼리","호랑이","곰","여우","늑대","너구리","침팬치","고릴라","참새","고슴도치","강아지","고양이","거북이","토끼","앵무새","하이에나","돼지","하마","원숭이","물소","얼룩말","치타",
                "악어","기린","수달","염소","다람쥐","판다");
        Collections.shuffle(nick);
        Collections.shuffle(name);
        return nick.get(0) + name.get(0);
    }
    
    public String getRandomId() {
        StringBuilder text = new StringBuilder();
        String ran = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for(int i = 0; i < 6; i++) {
            text.append(ran.charAt((int) (Math.random() * ran.length())));
        }
        return text.toString();
    }
    
    public String getRandomProfileImgUrl() {
        List<String> urls = Arrays.asList(
                "data:image/png;base64,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",
                "data:image/jpeg;base64,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",
                "data:image/jpeg;base64,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",
                "data:image/jpeg;base64,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",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSek9xoCOv2UaQ83aBK23db-Ear73BWExNT-w&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBn069tIAxgFLkgMuhU1aGrC3hivd-Srv4_A&usqp=CAU",
                "data:image/jpeg;base64,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",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSYDhNr7IDBc6RalxE6cASpcKT4Kg1MpVgVoWrla6b62nEbKXp7_NPNu_0swC82vDPbX8E&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR5Ajosknl2_Rpbc4Efri3nnVnJ6UVJNUyzBQ&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTatK-TFphcnuGUO7Ohk2gzynz0hCN3qXRXgwodZdnbKaS3JzMeUFTRCwWPbmp6VjkN9nQ&usqp=CAU",
                "data:image/jpeg;base64,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",
                "data:image/jpeg;base64,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",
                "https://blog.kakaocdn.net/dn/25lr5/btqzAi7Bx5x/u8DsP1s5gn44DhK94EVvv1/img.png",
                "https://mblogthumb-phinf.pstatic.net/MjAyMTAxMjJfMjI2/MDAxNjExMzAwNzg1MjIw.KYV1bj07BaG8t_YMIEaqeu6QU0MSXQoeAci-qlBM268g.lN2tmPTV_A-71EVRogMhRD82ahhIwqVKYr5dLf30KfUg.JPEG.deogeye/IMG_9953.JPG?type=w800",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQIwjQml0UXUVursA0QvcvmcIQ-t2TYe3iE5w&usqp=CAU",
                "data:image/jpeg;base64,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",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQZ2CA9xB_8gofn_7-MPAqx0sLL_QIfIUUq4w&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQx4BRAgDYqnblBquJviGvvLzwY7btF7RzZTQ&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1_7yTbjW0Z_LaxEg5oyH-ABn7TYjpYbfJmw&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRx1_8nOiHKTO6hPWMBh-koBb_JftglnXkdsg&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfLiGMlghdJ2q5WhfpbGBfVSrVYEw0RO2k6g&usqp=CAU"
        );
        Collections.shuffle(urls);
        return urls.get(0);
    }
    
    public Long randomLDownN(int n) {
        Random random = new Random();
        return (long)(random.nextInt(n) + 1);
    }
    
    public Integer randomIDownN(int n) {
        Random random = new Random();
        return (random.nextInt(n) + 1);
    }
    
    public String getRandomTitle(){
        List<String> title1 = Arrays.asList(
            "첫 번째 단계,",
            "비밀의 시작,",
            "모험의 시작,",
            "기초를 다지며,",
            "숨겨진 진실,",
            "발견된 세계,",
            "시간 속 여정,",
            "잃어버린 기억,",
            "미지의 탐색,",
            "숨겨진 길,",
            "가려진 이야기,",
            "잊혀진 전설,",
            "불가사의한 발견,",
            "깊은 숲 속으로,",
            "숨겨진 도시,",
            "오래된 비밀,",
            "잠들어 있는 과거,",
            "빛나는 별의 노래,",
            "잊혀진 섬,",
            "숲의 속삭임,",
            "바다의 전설,",
            "사라진 왕국,",
            "타임캡슐,",
            "고대의 비밀,",
            "숨겨진 통로,",
            "마법의 시작,",
            "전설의 귀환,",
            "신비한 여행,",
            "은밀한 임무,",
            "시간의 문,"
        );
        List<String> title2 = Arrays.asList(
            "시간 여행의 기술",
            "우주로의 여정",
            "고대 문명 탐험",
            "인공 지능의 미래",
            "심해의 비밀",
            "숲 속의 숨겨진 삶",
            "양자 세계의 탐험",
            "미래 도시의 삶",
            "외계 문명과의 만남",
            "환경 보호의 중요성",
            "인간과 로봇",
            "가상 현실의 세계",
            "우주 식민지화",
            "인류의 운명",
            "우주 여행의 비밀",
            "에너지의 미래",
            "인공 지능과 윤리",
            "기후 변화의 영향",
            "사이버 보안의 중요성",
            "나노 기술의 발전",
            "우주에서의 생존",
            "가상과 현실의 경계",
            "인간의 뇌와 기계",
            "생명 공학의 도전",
            "우주의 신비",
            "인간의 진화",
            "과학과 종교",
            "미래의 의학",
            "로봇 기술의 발전",
            "우주 전쟁의 가능성"
        );
        Collections.shuffle(title1);
        Collections.shuffle(title2);
        return title1.get(0) + " " + title2.get(0);
    }
    
    public String getRandomContent(){
        List<String> contentSentences = Arrays.asList(
            "<p>바람이 산을 넘어 강을 건너 평야를 스쳐 지나갔다.</p>",
            "<p>해질 무렵, 그늘진 숲속에는 고요함이 감돌았다.</p>",
            "<p>태양은 서쪽 하늘에서 천천히 기울기 시작했다.</p>",
            "<p>작은 마을에는 밤이 찾아와 조용함을 가져다주었다.</p>",
            "<p>그녀는 오래된 일기장을 천천히 넘겨보았다.</p>",
            "<p>별들이 밤하늘을 수놓으며 이야기를 시작했다.</p>",
            "<p>숲 속의 작은 오두막에는 불빛이 반짝이고 있었다.</p>",
            "<p>바다의 파도는 조용히 해변을 쓸고 지나갔다.</p>",
            "<p>오래된 나무 아래에는 깊은 그늘이 드리워져 있었다.</p>",
            "<p>고대의 비밀은 아직도 이 땅에 숨겨져 있었다.</p>",
            "<p>그는 먼 여행길에 오르기 위해 마지막 준비를 했다.</p>",
            "<p>하늘을 나는 새들이 자유롭게 날아다녔다.</p>",
            "<p>시간은 끊임없이 흘러가고 있었다.</p>",
            "<p>그녀는 조용한 호수를 바라보며 생각에 잠겼다.</p>",
            "<p>바람이 산을 넘어 강을 건너 평야를 스쳐 지나갔다. 그 바람은 먼 곳의 노래를 들려주었고, 그 소리에는 오래된 이야기와 잊혀진 전설이 담겨 있었다.</p>",
            "<p>해질 무렵, 그늘진 숲속에는 고요함이 감돌았다. 나뭇잎 사이로 스며드는 마지막 햇살이 길을 밝혔고, 숲은 저마다의 비밀을 속삭이기 시작했다.</p>",
            "<p>태양은 서쪽 하늘에서 천천히 기울기 시작했다. 그 빛은 세상의 모든 색을 더욱 선명하게 만들었고, 저녁의 순간은 마법처럼 모든 것을 변화시켰다.</p>",
            "<p>작은 마을에는 밤이 찾아와 조용함을 가져다주었다. 마을의 모든 집들은 불을 밝히고, 창문 너머로는 가족들의 웃음소리와 함께 행복이 가득했다.</p>",
            "<p>그녀는 오래된 일기장을 천천히 넘겨보았다. 각 페이지에는 지나간 날들의 추억과 꿈, 그리고 잊지 못할 순간들이 담겨 있었다.</p>",
            "<p>별들이 밤하늘을 수놓으며 이야기를 시작했다. 그 빛나는 별들 사이에서는 오래된 우주의 비밀이 숨겨져 있었고, 그것은 시간을 초월한 이야기를 들려주었다.</p>",
            "<p>숲 속의 작은 오두막에는 불빛이 반짝이고 있었다. 그 속에서는 따뜻한 이야기가 펼쳐졌고, 오두막의 주인은 모험과 꿈에 대해 이야기했다.</p>",
            "<p>바다의 파도는 조용히 해변을 쓸고 지나갔다. 그 파도 소리에는 오랜 바다의 기억이 담겨 있었고, 그것은 듣는 이에게 평화와 위안을 가져다주었다.</p>",
            "<p>작은 언덕 위에서는 세상이 달라 보였다.</p>",
            "<p>그녀는 고요한 바다를 바라보며 오래된 추억에 잠겼다.</p>",
            "<p>바람은 창문을 통해 부드럽게 방안으로 스며들었다.</p>",
            "<p>노을이 지는 하늘은 붉은 색으로 물들었다.</p>",
            "<p>그들은 오래된 성의 벽을 따라 걸었다.</p>",
            "<p>책장을 넘기는 소리가 조용한 도서관을 채웠다.</p>",
            "<p>그녀는 먼 곳을 바라보며 깊은 생각에 잠겼다.</p>",
            "<p>밤하늘은 별빛으로 가득 차 있었다.</p>",
            "<p>그는 오래된 지도를 펼쳐보며 다음 목적지를 찾았다.</p>",
            "<p>오래된 편지는 많은 이야기를 담고 있었다.</p>",
            "<p>그들은 오랜 시간 동안 서로를 기다려왔다.</p>",
            "<p>그녀의 눈빛은 별처럼 빛나고 있었다.</p>",
            "<p>산길을 따라 오르며 그들은 새로운 경치를 만끽했다.</p>",
            "<p>그녀는 오래된 그림 앞에서 잠시 멈춰 섰다.</p>",
            "<p>마법 같은 순간이 그들을 기다리고 있었다.</p>"
        );
        
        List<String> imageTags = Arrays.asList(
            "<img src='https://cdn.pixabay.com/photo/2016/09/10/17/18/book-1659717_1280.jpg' />",
            "<img src='https://cdn.pixabay.com/photo/2014/09/05/18/32/old-books-436498_1280.jpg' />",
            "<img src='https://cdn.pixabay.com/photo/2017/02/26/21/39/rose-2101475_1280.jpg' />",
            "<img src='https://cdn.pixabay.com/photo/2014/08/16/18/17/book-419589_1280.jpg' />",
            "<img src='https://cdn.pixabay.com/photo/2014/02/01/17/28/apple-256261_1280.jpg' />",
            "<img src='https://cdn.pixabay.com/photo/2016/03/27/19/32/book-1283865_1280.jpg' />",
            "<img src='https://cdn.pixabay.com/photo/2018/01/17/18/43/book-3088775_1280.jpg' />",
            "<img src='https://cdn.pixabay.com/photo/2016/01/27/04/32/books-1163695_1280.jpg' />",
            "<img src='https://cdn.pixabay.com/photo/2014/02/01/17/28/apple-256263_1280.jpg' />",
            "<img src='https://cdn.pixabay.com/photo/2020/04/28/18/33/key-5105878_1280.jpg' />"
        );
        
        
        StringBuilder content = new StringBuilder();
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 20; j++){
                Collections.shuffle(contentSentences);
                content.append(contentSentences.get(i));
            }
            Collections.shuffle(imageTags);
            content.append(imageTags.get(i));
        }
        
        return content.toString();
    }
    
    public String getRamdomCoverImgUrl(){
        List<String> urls = Arrays.asList(
            "https://i.ibb.co/CWdjT0m/2aa130fe-8ec2-480c-b285-8030312b9266.jpg",
            "https://i.ibb.co/cLpR9zS/2b0d223d-1e9a-4ad1-9470-84b45a586432.jpg",
            "https://i.ibb.co/GMkKD5r/2dfc1034-a1b2-41c6-ada6-a35b8abc9cf9.jpg",
            "https://i.ibb.co/SJdxC4W/8fa853d4-f339-4e0e-9f04-865aa391e231.jpg",
            "https://i.ibb.co/P5jNnRh/52d4ea07-0dc6-4ec8-a077-f44e290d3dbd.jpg",
            "https://i.ibb.co/vLB2J6k/72fac78d-05bb-48ba-a4ba-2a900f9f628f.jpg",
            "https://i.ibb.co/sW5Q6W7/77ee6ba4-c843-42de-a5c9-81cf031fe61d.jpg",
            "https://i.ibb.co/RNZFf3x/97d1a5ba-706c-46ad-8d63-543787d0720f.jpg",
            "https://i.ibb.co/7CbZck8/bb1fccfe-71fc-4df6-965c-40f08a2b70df.jpg",
            "https://i.ibb.co/HxJKpSc/de1de657-ec6a-4214-8878-7a37962e9a9d.webp",
            "https://i.ibb.co/j4rgWJY/e1b3d8c1-41b5-4534-ad09-e153f171b3dc.jpg",
            "https://i.ibb.co/KwGCTwS/edf2cb35-cda1-4479-ab81-24a101bc9d7e.jpg",
            "https://i.ibb.co/55w5tTk/fdcda997-153c-4f50-9c6f-859c35b29ac2.jpg>"
        );
        Collections.shuffle(urls);
        return urls.get(0);
    }
    
    public String getRandomComment(){
        List<String> comments = Arrays.asList(
            "이야기가 너무 재밌어요!",
            "이야기가 너무 재미없어요!",
            "이야기가 너무 슬퍼요!",
            "이야기가 너무 신기해요!",
            "이야기가 너무 흥미롭네요!",
            "이야기가 너무 재미있어요!",
            "이야기가 너무 재미없어요!",
            "이야기가 너무 슬퍼요!",
            "이야기가 너무 신기해요!",
            "이야기가 너무 흥미롭네요!",
            "이야기가 너무 재미있어요!",
            "이야기가 너무 재미없어요!",
            "이야기가 너무 슬퍼요!",
            "이야기가 너무 신기해요!",
            "이야기가 너무 흥미롭네요!",
            "이야기가 너무 재미있어요!",
            "이야기가 너무 재미없어요!",
            "이야기가 너무 슬퍼요!",
            "이야기가 너무 신기해요!",
            "이야기가 너무 흥미롭네요!",
            "이야기가 너무 재미있어요!",
            "이야기가 너무 재미없어요!",
            "이야기가 너무 슬퍼요!",
            "이야기가 너무 신기해요!",
            "이야기가 너무 흥미롭네요!",
            "이야기가 너무 재미있어요!",
            "이야기가 너무 재미없어요!"
        );
        
        Collections.shuffle(comments);
        return comments.get(0);
    }
}