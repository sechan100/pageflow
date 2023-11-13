/* eslint-disable @typescript-eslint/no-unused-vars */
import axios from 'axios';
import { ChapterSummary, Outline } from '../../types/types';
import { useRearrangeOutline } from '../../api/book-apis';
import { MutableRefObject } from 'react';
import flowAlert from '../../etc/flowAlert';


export interface IOutlineSidebarProps {
  children: React.ReactNode;
  bookId: number;
  queryClient: any;
  outlineBufferStatusReducer : [
    outlineBufferStatus : string,
    outlineBufferStatusDispatch : any
  ]
}


export default function OutlineSidebarWrapper(drillingProps : IOutlineSidebarProps) {


  return (
    <>
      <aside id="page-outline-sidebar" className="fixed z-10 top-0 left-0 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0">
        <div className="overflow-y-auto pt-12 pb-5 px-3 h-full bg-white border-r border-gray-200 dark:bg-gray-800 dark:border-gray-700">
          {drillingProps.children}
        </div>
      </aside>
      <AddChapterBtn {...drillingProps} />
      <div id="sidebar-placeholder" className="relative w-64 h-screen transition-transform -translate-x-full sm:translate-x-0"></div>
    </>
  );
}



interface IAddChapterBtnProps {
  bookId: number;
  queryClient: any;
  outlineBufferStatusReducer : [
    outlineBufferStatus : string,
    outlineBufferStatusDispatch : any
  ]
}


function AddChapterBtn(drillingProps : IAddChapterBtnProps) {

  const { bookId, queryClient, outlineBufferStatusReducer } = drillingProps;
  const { mutateAsync, isLoading, error } = useRearrangeOutline(bookId);

  const [outlineBufferStatus, outlineBufferStatusDispatch] = outlineBufferStatusReducer;

  // 서버에 Outline 데이터의 재정렬 업데이트 요청을 보내는 함수
  async function updateOutlineOnServer(outline : Outline){

    // isOutlineRearranged가 true인 경우에만 서버에 요청을 보낸다.
    if(outlineBufferStatus === 'mutated'){

      try{

        await mutateAsync(outline)
        flowAlert('success', "목차 정보가 저장되었습니다.");

        // 요청을 전달한 후에 성공적으로 업데이트 되었다면, outlineBufferStatus를 flushed로 변경한다.
        outlineBufferStatusDispatch({type: 'flushed'});

      } catch(error) {
        flowAlert('error', "목차 정보를 서버와 동기화하지 못했습니다.");
      }
    }
  }


  // 서버에서 새로운 Chapter 생성요청을 전달하고 받아온 생성된 Chapter를 react query가 관리하는 캐쉬에 반영.
  async function addChapter() {

    // 변경된 Outline 정보가 있다면 먼저 동기화
    updateOutlineOnServer(queryClient.getQueryData(['book', bookId]));

    const response = await axios.post(`/api/book/${bookId}/chapter`);

    if(response.status !== 200){
      throw new Error("새로운 챕터를 생성하지 못했습니다.");
    }

    // 새로 생성된 Chapter 데이터
    const newChapter =  response.data;

    // 새로 생성된 Chapter 데이터를 ChapterSummary 타입으로 변환
    const newChapterSummary : ChapterSummary = {
      id: newChapter.id,
      title: newChapter.title,
      sortPriority: newChapter.sortPriority,
      pages: newChapter.pages
    }

    queryClient.setQueryData(['book', bookId], (oldData : Outline) => {
      
      // 기존 챕터가 없던경우 새로운 챕터만 추가해서 반환
      if(!oldData.chapters){
        return {
          ...oldData,
          chapters: [
            newChapterSummary
          ]
        }
      } else {
        // 기존 챕터가 있던 경우 원래 있던거에 추가해서 반환
        return {
          ...oldData,
          chapters: [
            ...oldData.chapters,
            newChapterSummary
          ]
        }
      }
    });
  }


  return (
    <button type="button" onClick={addChapter} className="fixed z-20 top-4 left-40 px-5 py-2 text-xs font-medium text-center ext-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700">새 챕터</button>
  );
}