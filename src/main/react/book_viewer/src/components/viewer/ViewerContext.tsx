import { create } from "zustand";
import { useGetPage } from "../api/page-api";
import { Outline } from "../../types/types";
import { metaPageType, useLocationStore } from "../nav/PageCursor";
import { getChapterTitle } from "../nav/Navbar";
import { useEffect, useRef } from "react";
import Carousel from "./Carousel";



interface UseNavStore {
  isVisible: boolean;
  toggle: () => void;
}


export const useNavStore = create<UseNavStore>((set, get) => ({
  isVisible: false,
  toggle: () => {
    const { isVisible } = get();
    set({isVisible: !isVisible});
  }
}));




export default function ViewerContext({outline}: {outline: Outline}) {

  const { toggle } = useNavStore();
  const { location, metaPage } = useLocationStore();
  const getPageAsync = useGetPage(outline.id, getPageMap(outline));
  const currentPage = getPageAsync(location);
  const contentContainer = useRef<HTMLDivElement>(null);

  // 총 칼럼이 홀수개일 경우, 마지막 칼럼의 오른쪽은 빈 페이지여야한다. 근데 해결 방법이 마땅치 않아서 그냥 줄바꿈태그를 최소 한도로 넣어놓음.
  const lastColumnOffset = ("<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>");

  return (
    <div className="" onClick={toggle}>
      <div className="text-center py-20 sm:px-10 xl:px-52">
        { metaPage.isMetaPage && metaPage.type === metaPageType.CHAPTER_INIT &&
          <div className="text-2xl mt-20">{getChapterTitle(outline, location.chapterIdx)}</div>
        }
        <div className="text-justify">
          <Carousel container={contentContainer}>
            { !metaPage.isMetaPage &&
              <div
                id="viewer-page-content-container" 
                ref={contentContainer}
                style={{columnFill: "auto", columnGap: "8%"}} 
                className="select-none columns-2 h-[79vh] text-lg racking-wide leading-loose"
                dangerouslySetInnerHTML={{__html: currentPage.content + lastColumnOffset}}>
              </div>
            }
          </Carousel>
        </div>
      </div>
    </div>
  );


    // [chapterIdx, PageIdx] 구조인 ILocation 타입을 key, pageId를 value로 하는 map을 반환한다.
    function getPageMap(outline : Outline) : Map<string, number>{

      const pageMap = new Map<string, number>();
  
      outline.chapters.forEach((chapter, chapterIdx) => {
        chapter.pages?.forEach((page, pageIdx) => {
          pageMap.set(`${chapterIdx},${pageIdx}`, page.id);
        });
      });
  
      return pageMap;
    }

}
