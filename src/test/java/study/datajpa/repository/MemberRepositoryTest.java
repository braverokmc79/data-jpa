package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;


    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        Member member =new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }


    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 =memberRepository.findById(member1.getId()).get();
        Member findMember2 =memberRepository.findById(member2.getId()).get();
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);


        //리스트 조회 검증
        List<Member> all=memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count=memberRepository.count();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount=memberRepository.count();
        Assertions.assertThat(deleteCount).isEqualTo(0);

    }


    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }


    @Test
    public void testQuery(){
        Member m1=new Member("AAA", 10);
        Member m2 =new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result =memberRepository.findUser("AAA", 10);
        Assertions.assertThat(result.get(0)).isEqualTo(m1);
    }


    @Test
    public void findUsernameList(){
        Member m1=new Member("AAA",10);
        Member m2=new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList=memberRepository.findUsernameList();
        for(String s :usernameList){
            System.out.println("s = " + s);
        }
    }


    @Test
    public void findMemberDtoTest(){
        Team team =new Team("teamA");
        teamRepository.save(team);

        Member m1=new Member("AAA",10);
        Member m2=new Member("BBB",20);
        m1.setTeam(team);
        m2.setTeam(team);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<MemberDto> memberDtos=memberRepository.findMemberDto();
        for(MemberDto dto : memberDtos){
            System.out.println("회원 출력 = " + dto.toString());
        }
    }

    @Test
    public void findByNames() {
        Member m1=new Member("AAA",10);
        Member m2=new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result=memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for(Member member :result){
            System.out.println("member = " + member);
        }
    }

    //페이징 조건과 정렬 조건 설정
    @Test
    public void page() throws Exception{
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        //when
        PageRequest pageRequest =PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page=memberRepository.findByAge(10,pageRequest);

        //Page<MemberDto> dtoPage =page.map(m->new MemberDto());


        //then
        List<Member> content = page.getContent(); //조회된 데이터
        Assertions.assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        Assertions.assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        Assertions.assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
        Assertions.assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
    }



    @Test
    public void bulkUpdate() throws Exception{
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount=memberRepository.bulkAgePlus(20);

        //then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }



    @Test
    public void findMemberLazy() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));
        em.flush();
        em.clear();
        //when
        List<Member> members = memberRepository.findAll();
        //then
        for (Member member : members) {

            System.out.println("시작============================================================");
            member.getTeam().getName();
            // 다음과 같이 지연 로딩 여부를 확인할 수 있다
            //Hibernate 기능으로 확인
            Hibernate.initialize(member.getTeam());

            //JPA 표준 방법으로 확인
            PersistenceUnitUtil util =  em.getEntityManagerFactory().getPersistenceUnitUtil();
            System.out.println( "isLoaded : " + util.isLoaded(member.getTeam()) );
            System.out.println("끝============================================================");
        }
    }



    @Test
    public void queryHint() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();
        //when
        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");
        em.flush(); //Update Query 실행X
    }




}