import { useRef } from "react";
import Carousel from "./Carousel";
import PageCursor from "../nav/PageCursor";



export default function PageViewer({quillValue, viewPortHeight}: {quillValue: string, viewPortHeight: number}) {

  const carouselContentRef = useRef<HTMLDivElement>(null);


  // 총 칼럼이 홀수개일 경우, 마지막 칼럼의 오른쪽은 빈 페이지여야한다. 근데 해결 방법이 마땅치 않아서 그냥 줄바꿈태그를 최소 한도로 넣어놓음.
  const lastColumnOffset = ("<p class='invisible h-full'>ㅋㅋㅋ</p>");

  return (
    <div className="relative">
      <div className="text-center">
        <div className="text-justify">
          <Carousel carouselContentRef={carouselContentRef}>
            <div
              id="carousel-content"
              ref={carouselContentRef}
              style={{columnFill: "auto", columnGap: "8%"}} 
              className={`select-none columns-2 h-[${viewPortHeight}vh] text-md}`}
              // xss 보안 문제 해결하기 -> 라이브러리
              dangerouslySetInnerHTML={{__html: quillValue + lastColumnOffset}}>
            </div>
          </Carousel>
        </div>
      </div>
      <PageCursor />
    </div>
  );

}
