/* eslint-disable react-hooks/exhaustive-deps */

import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { IPage, PageMutation} from "../../../../types/types";
import { create } from "zustand";
import { useParams } from "react-router-dom";
import { useGetPageQuery } from "../../../../api/page-api";
import QuillEditor, { useQuillFocusStore } from "./QuillEditor";
import PageViewer from "./viewerComponents/PageViewer";

interface PageMutationStore {
  payload: PageMutation[];
  isMutated: boolean;
  resetMutation: () => void;
  isLoading: boolean;
  dispatchs: {
    updatePage: (pageMutation : PageMutation) => void;
  }
}

export const usePageMutationStore = create<PageMutationStore>((set : any) => ({
  payload: [], // Page 정보 변경사항을 서버에 요청하기 위한 객체
  isMutated: false, // 변경사항이 존재하는지 여부
  resetMutation: () => set(
    (state : any) => ({
      ...state,
      payload: [],
      isMutated: false
    })
  ),
  isLoading: false, // 서버에 전달한 요청이 진행중인지의 여부. 기본적으로 false이지만, [true로 바뀌는 순간부터, 다시 false가 되는 순간]까지 서버에 요청중임을 의미한다.
  dispatchs: { // payload 상태를 변경하는 dispatch 함수들. 내부적으로 isMutated를 true로 변경하는 로직을 포함한다.
    updatePage: ({id, title, content} : PageMutation) => {
      set((state : PageMutationStore) : PageMutationStore => {

        // 해당 PageId를 가진 변경사항이 존재하는 경우, 해당 부분을 수정, 
        // 존재하지 않는 경우, 새로운 PageMutation을 추가한다.
        const isExist = state.payload.find(page => page.id === id);

        // 기존 변경사항이 존재하는 경우
        if(isExist){
          
          return {
            ...state,
            payload: state.payload.map(page => {
              if(page.id === id) return {...page, title: title, content: content} 
              else return page;
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
                title: title,
                content: content
              }
            ],
            isMutated: true
          }
        }
      });
    },
  },
}));



export default function PageForm(){
  const { pageId } = useParams();
  const page : IPage = useGetPageQuery(Number(pageId));
  const pageStore = usePageMutationStore();
  const [localPage, setLocalPage] : [IPage, Dispatch<SetStateAction<IPage>>] = useState<IPage>(page);
  const [isViewMode, setIsViewMode] = useState<boolean>(false);
  const [editMode, setEditMode] = useState<string>("base"); // base, content
  const {quillFocus} = useQuillFocusStore(); // quill editor의 포커스를 잡기 위한 zustand store


  // outline 데이터의 변경시, 이미 선언된 state인 ChapterMutation의 상태를 업데이트하기 위함
  useEffect(() => {
    if(page){
      setLocalPage({
        id: page.id,
        title: page.title,
        content: page.content,
      });
      quillFocus();
    }
  }, [page]);


  
  // 로컬 데이터의 변경사항을 zustand store에 업데이트
  useEffect(() => {

    const currentPageMutation = {
      id: localPage.id, 
      title: localPage.title, 
      content: localPage.content
    }

    if(localPage.id !== 0){
      // local의 title 데이터가 존재하면서 outline의 title 데이터와 다를 경우 => title 변경사항이 존재
      if(localPage.title && isTitleChanged(localPage.title)){
        pageStore.dispatchs.updatePage(currentPageMutation);
      }

      if(localPage.content !== page.content){
        pageStore.dispatchs.updatePage(currentPageMutation);
      }
    }
  }, [localPage]);


  return (
    <>
      <div>
        <div className="grid max-w-xs grid-cols-2 gap-1 p-1 mx-auto my-2 bg-gray-200 rounded-lg" role="group">
            <button type="button" onClick={() => setEditMode("base")} className={ (editMode === "base" ? "text-white bg-gray-900" : "text-gray-900 hover:bg-gray-300") + " px-5 py-1.5 text-xs font-medium rounded-lg"}>
              페이지 정보
            </button>
            <button type="button" onClick={() => setEditMode("content")} className={ (editMode === "base" ? "text-gray-900 hover:bg-gray-300" : "text-white bg-gray-900") + " px-5 py-1.5 text-xs font-medium rounded-lg"}>
              내용 수정
            </button>
        </div>
      </div>

      { editMode === "base" && 
        <div className="mt-16">
          <div>
            {/* title */}
            <div className="sm:col-span-2">
                <label htmlFor="title" className="block mb-2 text-md font-medium text-gray-900">페이지 제목</label>
                <input value={localPage.title ? localPage.title : ''} onChange={handleTitleChange} type="text" name="title" id="title" className="bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="책 제목을 입력해주세요." />
            </div>
          </div>
          <br/><br/>
        </div>
      }

      { editMode === "content" &&
        <div>
          <button type="button" onClick={() => {setIsViewMode(!isViewMode); quillFocus();}} className="text-gray-900 hover:text-white border border-gray-800 hover:bg-gray-700 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2">
            { isViewMode ? "편집 모드" : "미리보기 모드"}
          </button>
          <div className="h-[75vh] w-[120vh]">
            {isViewMode ? 
              <PageViewer viewPortHeight={65} quillValue={localPage.content} /> : 
              <QuillEditor viewPortHeight={65} quillValue={localPage.content} setContent={handleContentChange} />
            }
          </div>
        </div>
      }

    </>
  );

  function handleContentChange(value : string){
    setLocalPage(state => {
      return {
        ...state,
        content: value
      }
    });
  }


  // 현재 서버 상태에 저장된 데이터와 다른지...
  function isTitleChanged(title : string) : boolean {
    return page?.title !== title;
  }

  function handleTitleChange(e : any){
    setLocalPage(state => {
      return {
        ...state,
        title: e.target.value
      }
    });
  }


}
