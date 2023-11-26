/* eslint-disable react-hooks/exhaustive-deps */

import { create } from "zustand";
import { ChapterSummary } from "../../../../../types/types";
import { useCarouselStore } from "../viewerComponents/Carousel";
import { useEffect } from "react";



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



export default function PageCursor() {

  const { carouselIdx, prevCarousel, nextCarousel, isLastCarousel} = useCarouselStore();

  
  useEffect(() => {
    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [carouselIdx, isLastCarousel]);



  return (
    <>
      <div className="absolute top-[30%]">

        <span className="fixed" onClick={goPrev}>
          <div className="relative w-20 h-44 hover:bg-[#FDFDFD] opacity-50 rounded-full hover:bg-gray-100 cursor-pointer">
            <svg className="absolute opacity-100 top-[45%] left-[40%] w-[20px] h-[20px] text-gray-800" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 8 14">
              <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M7 1 1.3 6.326a.91.91 0 0 0 0 1.348L7 13"/>
            </svg>
          </div>
        </span>

        <span className="fixed left-[87%]" onClick={goNext}>
          <div className="relative w-20 h-44 hover:bg-[#FDFDFD] opacity-50 rounded-full hover:bg-gray-100 cursor-pointer">
            <svg className="absolute opacity-100 top-[45%] left-[40%] w-[20px] h-[20px] text-gray-800" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 8 14">
              <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 13 5.7-5.326a.909.909 0 0 0 0-1.348L1 1"/>
            </svg>
          </div>
        </span>

      </div>
    </>
  );


  
  function goPrev() {
    // 캐러셀이 처음 캐러셀이 아니고, metaPage도 아니라면 한장 뒤로간다.
    if(carouselIdx !== 0){
      prevCarousel();
    }
  }

  function goNext(){
    // 캐러셀이 마지막 캐러셀이 아니라면 캐러셀를 한장 넘긴다.
    if(!isLastCarousel){
      nextCarousel();
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
