/* eslint-disable react-hooks/exhaustive-deps */
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { Outline } from "../../types/types";
import ProgressionBar from "./ProgressionBar";
import { useNavStore } from "../viewer/ViewerContext";
import { ILocation } from "./PageCursor";




interface NavbarProps {
  location: ILocation;
  outline: Outline;
}




export default function Navbar({location, outline} : NavbarProps) {

  const [absolutePage, setAbsolutePage] : [number, Dispatch<SetStateAction<number>>] = useState<number>(0);
  const { isVisible } = useNavStore();

  // location 변화에 맞춰서 absolutePage를 동기화.
  useEffect(() => {
    setAbsolutePage(getAbsolutePage(outline, location));
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
      return Math.floor(absolutePage / totalPage() * 100);
    }
  }


  return (<>
      { isVisible &&
        <div className="text-center fixed w-full bg-gray-100">
          <span>챕터: {getChapterTitle(outline, location.chapterIdx)}</span> 
          <br />
          <span>페이지: {getPageTitle(outline, location) + `(${absolutePage}/${totalPage()})`}</span>
          <ProgressionBar progressPercentage={progressPercentage() as number} />
        </div>
      }
    </>
  );



  // 모든 페이지의 배열에서 현재 페이지의 인덱스를 찾는다.
  function getAbsolutePage(outline : Outline, {chapterIdx, pageIdx} : ILocation){
    let absolutePage = 0;
    for(let i = 0; i < chapterIdx; i++){
      if(outline.chapters){
        absolutePage += outline.chapters[i].pages?.length as number;
      }
    }
    absolutePage += pageIdx;
    return absolutePage;
  }

  function getChapterTitle(outline : Outline, chapterIdx : number){
    if(outline.chapters){
      return outline.chapters[chapterIdx].title;
    }
  }

  function getPageTitle(outline : Outline, {chapterIdx, pageIdx} : ILocation){
    if(outline.chapters){
      return outline.chapters[chapterIdx].pages?.[pageIdx].title;
    }
  }

}