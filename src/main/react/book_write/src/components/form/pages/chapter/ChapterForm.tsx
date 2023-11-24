/* eslint-disable react-hooks/exhaustive-deps */

import { useContext, useEffect, useReducer } from "react";
import { QueryContext } from "../../../../App";
import { useGetOutlineQuery } from "../../../../api/outline-api";
import { ChapterMutation, ChapterSummary, Outline } from "../../../../types/types";
import { create } from "zustand";
import { useParams } from "react-router-dom";

interface ChapterMutationStore {
  payload: ChapterMutation[];
  isMutated: boolean;
  resetMutation: () => void;
  isLoading: boolean;
  dispatchs: {
    updateChapter: (update: ChapterMutation) => void;
  }
}

export const useChapterMutationStore = create<ChapterMutationStore>((set : any) => ({
  payload: [], // 챕터 정보 변경사항을 서버에 요청하기 위한 객체
  isMutated: false, // 변경사항이 존재하는지 여부
  resetMutation: () => set(
    (state : any) => ({
      ...state,
      payload: [],
      isMutated: false
    })
  ),
  isLoading: false, // 서버에 전달한 요청이 진행중인지의 여부. 기본적으로 false이지만, [true로 바뀌는 순간부터, 다시 false가 되는 순간]까지 서버에 요청중임을 의미한다.
  dispatchs: { // bookMutation 상태를 변경하는 dispatch 함수들. 내부적으로 isMutated를 true로 변경하는 로직을 포함한다.

    updateChapter : ({id, title} : ChapterMutation) => {
      set((state : ChapterMutationStore) : ChapterMutationStore => {

        // 해당 chapterId를 가진 변경사항이 존재하는 경우, 해당 부분을 수정, 
        // 존재하지 않는 경우, 새로운 chapterMutation을 추가한다.
        const isExist = state.payload.find(chapter => chapter.id === id);

        // 기존 변경사항이 존재하는 경우
        if(isExist){
          
          return {
            ...state,
            payload: state.payload.map(chapter => {
              if(chapter.id === id) return {...chapter, title: title} 
              else return chapter;
            }),
            isMutated: true
          }

        // 기존 변경사항이 존재하지 않는 경우  
        } else {
          return {
            ...state,
            payload: [
              ...state.payload,
              {
                id: id,
                title: title
              }
            ],
            isMutated: true
          }
        }
      });
    },

  },
}));



export default function ChapterForm(){
  const { chapterId } = useParams();
  const { bookId } = useContext(QueryContext);
  const outline : Outline = useGetOutlineQuery(bookId);
  const chapter : ChapterSummary | null = getChapterById(Number(chapterId)) as ChapterSummary | null;
  const chapterStore = useChapterMutationStore();
  const [localChapter, localChapterDispatch] : [ChapterMutation, any] = useReducer(localChapterReducer, chapterStore.payload); // zustand store에 변경사항을 업데이트하기 전에 임시로 저장하는 로컬 상태

  // outline 데이터의 변경시, 이미 선언된 state인 ChapterMutation의 상태를 업데이트하기 위함
  useEffect(() => {
    if(outline){
      localChapterDispatch({type: 'TITLE', payload: chapter ? chapter.title : null});
    }
  }, [outline, chapterId]);

  
  // 로컬 데이터의 변경사항을 zustand store에 업데이트
  useEffect(() => {

    // local의 title 데이터가 존재하면서 outline의 title 데이터와 다를 경우 => title 변경사항이 존재
    if(localChapter.title && isTitleChanged(localChapter.title)) chapterStore.dispatchs.updateChapter({id: Number(chapterId), title: localChapter.title});

  }, [localChapter]);


  return (
    <div className="mt-16">
      <div>
        {/* title */}
        <div className="sm:col-span-2">
            <label htmlFor="title" className="block mb-2 text-md font-medium text-gray-900">챕터 제목</label>
            <input value={localChapter.title ? localChapter.title : ''} onChange={handleTitleChange} type="text" name="title" id="title" className="bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="책 제목을 입력해주세요." />
        </div>
      </div>
    </div>
  );



  function localChapterReducer(state : ChapterMutation, action : {type : string, payload : string }) : any {
    switch(action.type){
      case 'TITLE':
        return {
          ...state,
          title: action.payload as string
        }
      default:
        return state;
    }
  }

  // 현재 서버 상태에 저장된 데이터와 다른지...
  function isTitleChanged(title : string) : boolean {
    return chapter?.title !== title;
  }

  function getChapterById(chapterId : number) {
    return outline.chapters?.find(chapter => chapter.id === chapterId);
  }

  function handleTitleChange(e : any){
    localChapterDispatch({type: "TITLE", payload: e.target.value});
  }
}