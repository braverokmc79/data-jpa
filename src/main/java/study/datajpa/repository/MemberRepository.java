package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();
    //생략 가능
   // @Query(name = "Member.findByUsername")  
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age =:age ")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);



    @Query("select m.username from Member  m ")
    List<String> findUsernameList();


    @Query("select new study.datajpa.repository.MemberDto(m.id, m.username, t.name ) " +
            " from Member m join m.team t")
    List<MemberDto> findMemberDto();



    @Query("select m from Member m where m.username = :name")
    Member findMembers(@Param("name") String username);


    @Query("select m from Member m where m.username in  :names")
    List<Member> findByNames(@Param("names") List<String> names);


    List<Member> findListByUsername(String name); //컬렉션

    Member findMemberByUsername(String name); //단건

    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    Page<Member> findPageByUsername(String name, Pageable pageable); //count 쿼리 사용

    Slice<Member> findSliceByUsername(String name, Pageable pageable); //count 쿼리 사용 안함


    List<Member> findListByUsername(String name, Pageable pageable); //count 쿼리 사용 안함

    List<Member> findListByUsername(String name, Sort sort);

    Page<Member> findByAge(int age, Pageable pageable);



    @Query(value="select m from Member m",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);


}
