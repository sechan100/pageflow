/* eslint-disable react-hooks/exhaustive-deps */
import { create } from "zustand";
import { useNavStore } from "../viewer/ViewerContext";
import { ChapterSummary } from "../../types/types";
import { useEffect } from "react";
import { useCarouselStore } from "../viewer/Carousel";


export interface ILocation {
  chapterIdx: number;
  pageIdx: number;
}

export interface UseLocationStore {
  totalChapters: number;
  chapterVolumes: number[];
  metaPage: { isMetaPage: boolean, type: string, prevLocation : ILocation | null, nextLocation : ILocation | null};
  location : ILocation;
  setLocation: (location : ILocation) => void;
  setTotalChapters: (totalChapters: number) => void;
  setChapterVolumes: (chapters: ChapterSummary[]) => void;
  next: () => void;
  prev: () => void;
}


export const metaPageType = {
  NONE: "NONE",
  BOOK_COVER: "BOOK_COVER",
  CHAPTER_INIT: "CHAPTER_INIT",
  BOOK_END: "BOOK_END"
}

const metaPageNone = { isMetaPage: false, type: metaPageType.NONE, prevLocation : {chapterIdx: 0, pageIdx: 0}, nextLocation : {chapterIdx: 0, pageIdx: 0} }


export const useLocationStore = create<UseLocationStore>((set, get) => ({
  totalChapters: 0, // 총 chapter 수
  chapterVolumes: [], // 한 chapter 안에있는 pages의 length
  metaPage: { isMetaPage: false, type: metaPageType.NONE, prevLocation : {chapterIdx: 0, pageIdx: 0}, nextLocation : {chapterIdx: 0, pageIdx: 0} },
  location: { chapterIdx: 0, pageIdx: 0 },
  setLocation: (location : ILocation) => {
    set({
      location : {
        chapterIdx: location.chapterIdx,
        pageIdx: location.pageIdx,
      }
    });
  },
  setTotalChapters: (totalChapters: number) => {
    set({totalChapters});
  },
  setChapterVolumes: (chapters: ChapterSummary[]) => {
    const chapterVolumes : number[] = chapters.map(chapter => chapter.pages.length);
    set({chapterVolumes});
  },
  next: () => {
    const {location, metaPage, chapterVolumes } = get();

    // 현재 metaPage가 보여지고 있는 경우
    if(metaPage.isMetaPage){

      // metaPage가 책 끝이라면 '아무 일'
      if(metaPage.type === metaPageType.BOOK_END){
        return;
      } else if(metaPage.type === metaPageType.BOOK_COVER){
        set({
          metaPage: {
            isMetaPage: true,
            type: metaPageType.CHAPTER_INIT,
            prevLocation: null,
            nextLocation: {chapterIdx: 0, pageIdx: 0}
          }
        });
        return;
      }

      set({
        location: metaPage.nextLocation as ILocation,
        metaPage: metaPageNone
      });
      return;
    }

    // 아직 Chapter에 보여줄 Page가 남은 경우
    if(location.pageIdx < chapterVolumes[location.chapterIdx] - 1){
      set({
        location : {
          chapterIdx: location.chapterIdx,
          pageIdx: location.pageIdx + 1,
        }
      });

    // Chapter가 끝난 경우
    } else {
      // 다음 Chapter로 넘어가기
      if(location.chapterIdx < get().totalChapters - 1){
        set({
          location: {chapterIdx: location.chapterIdx + 1, pageIdx: 0 },
          metaPage: {
            isMetaPage: true,
            type: metaPageType.CHAPTER_INIT,
            prevLocation: location,
            nextLocation: {chapterIdx: location.chapterIdx + 1, pageIdx: 0 }
          }
        });
      // 책 마지막 metaPage로 넘어가기
      } else {
        set({
          metaPage: { 
            isMetaPage: true, 
            type: metaPageType.BOOK_END,
            prevLocation: {chapterIdx: location.chapterIdx, pageIdx: location.pageIdx},
            nextLocation: null
          }
        });
      }
    }
  },
  prev: () => {
    const {location, metaPage, chapterVolumes} = get();

    // 현재 metaPage가 보여지고 있는 경우
    if(metaPage.isMetaPage){

      // metaPage가 책 커버라면 '아무 일'
      if(metaPage.type === metaPageType.BOOK_COVER){
        return;
      } else if(metaPage.type === metaPageType.CHAPTER_INIT && metaPage.prevLocation === null){
        set({
          metaPage: {
            isMetaPage: true,
            type: metaPageType.BOOK_COVER,
            prevLocation: null,
            nextLocation: {chapterIdx: 0, pageIdx: 0}
          }
        });
        return;
      }

      set({
        location: metaPage.prevLocation as ILocation,
        metaPage: metaPageNone
      });
      return;
    }

    // 아직 Chapter에 이전 page가 존재하는 경우
    if(location.pageIdx > 0){
      set({
        location : {
          chapterIdx: location.chapterIdx,
          pageIdx: location.pageIdx - 1,
        }
      });

    // Chapter의 첫 페이지인 경우
    } else {
      // 책 커버 metaPage로 넘어가기
      if(location.chapterIdx === 0){
        set({
          metaPage: { 
            isMetaPage: true, 
            type: metaPageType.CHAPTER_INIT,
            prevLocation: null,
            nextLocation: {chapterIdx: location.chapterIdx, pageIdx: location.pageIdx}
          }
        });
      // 이전 Chapter의 metaPage로 넘어가기
      } else {
        set({
          location: {chapterIdx: location.chapterIdx , pageIdx: chapterVolumes[location.chapterIdx - 1] - 1 },
          metaPage: { 
            isMetaPage: true, 
            type: metaPageType.CHAPTER_INIT,
            prevLocation: {chapterIdx: location.chapterIdx - 1, pageIdx: chapterVolumes[location.chapterIdx - 1] - 1},
            nextLocation: {chapterIdx: location.chapterIdx, pageIdx: location.pageIdx}
          }
        });
      }
    }
  },
}));




export default function PageCursor() {

  const {isVisible} = useNavStore();
  const { location, metaPage, prev, next} = useLocationStore();
  const { carouselIdx, isLastCarousel, prevCarousel, nextCarousel, resetCarouselIdx, setIsLastCarousel } = useCarouselStore();

  
  useEffect(() => {
    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [isLastCarousel, location, metaPage]);



  return (<>
    {isVisible &&
    <div className="fixed top-[37%] left-20">

      <span className="fixed" onClick={goPrev}>
        <div className="relative w-20 h-44 hover:bg-[#FDFDFD] opacity-50 rounded-full hover:bg-gray-100 cursor-pointer">
          <svg className="absolute opacity-100 top-[45%] left-[40%] w-[20px] h-[20px] text-gray-800" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 8 14">
            <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M7 1 1.3 6.326a.91.91 0 0 0 0 1.348L7 13"/>
          </svg>
        </div>
      </span>

      <span className="fixed left-[90%]" onClick={goNext}>
        <div className="relative w-20 h-44 hover:bg-[#FDFDFD] opacity-50 rounded-full hover:bg-gray-100 cursor-pointer">
          <svg className="absolute opacity-100 top-[45%] left-[40%] w-[20px] h-[20px] text-gray-800" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 8 14">
            <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 13 5.7-5.326a.909.909 0 0 0 0-1.348L1 1"/>
          </svg>
        </div>
      </span>

    </div>
    }
    </>
  );


  function goPrev() {
    // 내부 캐러셀에서 마지막 페이지가 아니라면 innerPage를 한장 넘긴다.
    if(carouselIdx !== 0 && !metaPage.isMetaPage){
      prevCarousel();
      if(isLastCarousel){
        setIsLastCarousel(false);
      }
      return;

    } else {
      prev(); // 다음 페이지로 이동.
      resetCarouselIdx(); // 새로운 페이지로 넘어가고, isLastInnerPage를 false로 초기화
    }
  }

  function goNext(){
    // 내부 캐러셀에서 마지막 페이지가 아니라면 innerPage를 한장 넘긴다.
    if(!isLastCarousel && !metaPage.isMetaPage){
      nextCarousel();
      return;

    } else {
      next(); // 다음 페이지로 이동.
      resetCarouselIdx(); // 새로운 페이지로 넘어가고, isLastInnerPage를 false로 초기화
    }
  }


  function handleKeyDown(e: KeyboardEvent) {
    if(e.key === "ArrowLeft"){
      goPrev();
    } else if(e.key === "ArrowRight"){
      goNext();
    }
  }

}

