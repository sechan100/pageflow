import { create } from "zustand";
import { Outline } from "../../../types/types";
import { useLocation } from "react-router-dom";
import { useEffect, useRef, useState } from "react";


interface UseCreatePageStore {
  isMutated : boolean;
  chapterId: number;
  resetMutation: () => void;
  requestCreatePage : (chapterId : number) => void;
}

export const useCreatePageStore = create<UseCreatePageStore>((set : any) => ({
  isMutated : false,
  chapterId: 0,
  resetMutation: () => set(
    (state : any) => ({
      ...state,
      isMutated: false
    })
  ),
  requestCreatePage : (chapterId) => {
    set((state : any) => ({
      isMutated: true,
      chapterId
    }));
  }
}));


export default function NewPageBtn({outline: localOutline} : {outline : Outline}) {

  const { requestCreatePage: requestCreateChapter } = useCreatePageStore();
  const location = useLocation();
  const [isNewPageBtnVisible, setIsNewPageBtnVisible] = useState(false); // 새 페이지 버튼을 보여줄지 여부
  const chapterIdRef = useRef(0);

  // uri에 Chapter가 포함되어있다면, 새 페이지를 생성할 Chapter를 특정할 수 있으므로, 새 페이지 버튼을 보여준다.
  // 그리고 만약 버튼이 보이는 상태라면 해당 Chapter를 특정할 수 있도록, chapterId를 저장한다.
  useEffect(() => {
    const isFormPageOfChapterOrPage = location.pathname.includes('chapter');
    setIsNewPageBtnVisible(isFormPageOfChapterOrPage);
    if(isFormPageOfChapterOrPage){
      chapterIdRef.current = Number(location.pathname.split('chapter/')[1].split("/")[0]) // uri에서 chapterId를 추출
    }
  }, [location]);


  return (
    <>
      {
        isNewPageBtnVisible && 
        <button type="button" onClick={() => requestCreateChapter(chapterIdRef.current)} className="absolute z-20 top-5 px-1 md:px-5 right-[7%] py-2 text-xs font-medium text-center ext-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700">
          새 페이지
        </button>
      }
    </>
  );
}