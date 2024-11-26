# 융합프로젝트: PopFlix 영화 리뷰 서비스 
> LG U+ 유레카 과정에서 최종 융합 프로젝트| 영화 리뷰 서비스를 제공합니다. <br />

<br />

![image](https://github.com/user-attachments/assets/a2003f2d-973b-4fcc-a799-0157abe5700d)


## _Intro._
- 유저는 로그인하고 장르 기반 영화를 추천받을 수 있습니다. 
- 유저는 특정 영화에 리뷰와 댓글을 남길 수 있으며, 각 리뷰와 댓글에 '좋아요'를 표시할 수 있습니다. 
- 유저는 관심 영화 보관함을 만들 수 있으며, 다른 유저는 공개된 보관함에 대해 '좋아요'를 누를 수 있습니다.   

<br />

## _Documents._
- [회의록](https://www.notion.so/13cd57a9389f80f2b2ddda9396457186)
- [지라](https://tldms1176.atlassian.net/jira/software/projects/CT/boards/6/backlog?epics=visible)

<br />

## _ER Diagram._
- [ER Diagram](https://www.erdcloud.com/d/3yofqspcbKvXFAhqe)

<br />

## _Stacks._
> Backend
- Java 17, SpringBoot 3.5
- Spring Security, JWT, Oauth2
- Spring Data JPA
- MySQL
- Redis
- JUnit 5, Mockito


<br />

## _Member._

<div align="center">
  
| **임민아** | **정회헌** | **최준하** |
| :------: |  :------: | :------: |
| [<img src="https://avatars.githubusercontent.com/u/146914181?v=4" height=150 width=150> <br/> @01MINAH](https://github.com/01MINAH) | [<img src="https://avatars.githubusercontent.com/u/163866522?v=4" height=150 width=150> <br/> @JeongHhH](https://github.com/JeongHhH) | [<img src="https://avatars.githubusercontent.com/u/128604591?v=4" height=150 width=150> <br/> @choijh0309](https://github.com/choijh0309) |

</div>

#### 임민아
- Movie CRUD
- Movie 상세보기
- Movie 추천
- Movie '별점', '좋아요' 처리
- Movie Storage CRUD
- Test Code 작성

#### 정회헌
- Auth (login/logout)
- Auth Test Code 작성
- 회원 CRUD 
- Test Code 작성

#### 최준하
- Review CRUD
- Comment CRUD
- Review, Comment '좋아요' 처리
- PhotoReview CRUD
- Notification
- Report
- Test Code 작성

<br />

## _Achievements._

ERD 작성 시 원칙에 따라 작성했습니다. 좌측 상단부터 격자 형식으로 핵심 테이블 배치하고, 연관 및 부가, 이력 형질에 따라 테이블의 색상이 나뉩니다.  

![image](https://github.com/user-attachments/assets/f001bdde-f2e1-4fb5-9a8d-725a01391361)


