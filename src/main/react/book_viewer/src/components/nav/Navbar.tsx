/* eslint-disable react-hooks/exhaustive-deps */
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { Outline } from "../../types/types";
import ProgressionBar from "./ProgressionBar";
import { useNavStore } from "../viewer/ViewerContext";
import { ILocation, metaPageType, useLocationStore } from "./PageCursor";
import { ToMainBtn } from "./ToMainBtn";




interface NavbarProps {
  outline: Outline;
}




export default function Navbar({outline} : NavbarProps) {

  const [absolutePage, setAbsolutePage] : [number, Dispatch<SetStateAction<number>>] = useState<number>(0);
  const {location, metaPage} = useLocationStore();
  const { isVisible } = useNavStore();

  // location 변화에 맞춰서 absolutePage를 동기화.
  useEffect(() => {
    setAbsolutePage(getAbsolutePage(outline, location, metaPage));
  }, [location]);


  // 전체 페이지 수를 구한다.
  const totalPage = () => {
    const totalPage = outline.chapters?.reduce((acc, chapter) => acc + (chapter.pages?.length as number), 0);
    if(totalPage){
      return totalPage;
    } else {
      return -1;
    }
  } 


  const progressPercentage = () => {
    if(outline.chapters){
      return Math.floor((absolutePage + 1) / totalPage() * 100);
    }
  }


  return (<>
      { isVisible &&
        <div className="text-center fixed w-full bg-gray-100 select-none pt-3">
          <ToMainBtn />
          <span>챕터: {getChapterTitle(outline, location.chapterIdx)}</span> 
          <br />
          <span>{
              metaPage.isMetaPage
                ? (metaPage.type === metaPageType.BOOK_COVER
                    ? "표지"
                    : metaPage.type === metaPageType.CHAPTER_INIT
                    ? " "
                    : metaPage.type === metaPageType.BOOK_END
                    ? "마치며"
                    : "메타 페이지")
                : `페이지: ${absolutePage + 1}/${totalPage()}`}
          </span>
          <ProgressionBar progressPercentage={progressPercentage() as number} />
        </div>
      }
    </>
  );



  // 모든 페이지의 배열에서 현재 페이지의 인덱스를 찾는다.
  function getAbsolutePage(outline: Outline, location: ILocation, metaPage: any) {
    let absolutePage = 0;
  
    if (outline.chapters) {
      // 메타 페이지가 챕터의 시작을 나타내는 경우
      if (metaPage.isMetaPage && metaPage.type === "CHAPTER_INIT") {
        for (let i = 0; i < location.chapterIdx; i++) {
          absolutePage += outline.chapters[i].pages?.length || 0;
        }
        return absolutePage;
      }
  
      // 일반 페이지의 경우
      for (let i = 0; i < location.chapterIdx; i++) {
        absolutePage += outline.chapters[i].pages?.length || 0;
      }
    }
  
    absolutePage += location.pageIdx;
    return absolutePage;
  }
}

export function getChapterTitle(outline : Outline, chapterIdx : number){
  if(outline.chapters){
    return outline.chapters[chapterIdx].title;
  }
}

export function getPageTitle(outline: Outline, { chapterIdx, pageIdx }: ILocation) {
  if (outline.chapters && chapterIdx >= 0 && chapterIdx < outline.chapters.length) {
    const chapter = outline.chapters[chapterIdx];
    if (chapter.pages && pageIdx >= 0 && pageIdx < chapter.pages.length) {
      return chapter.pages[pageIdx].title;
    }
  }
  // 유효하지 않은 인덱스나 메타 페이지의 경우 대체 텍스트 반환
  return "유효하지 않은 페이지";
}