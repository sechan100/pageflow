import React, { useRef } from "react";
import Page from "./Page";
import { useState } from "react";
import {ChapterSummary} from '../../types/types'
import { Droppable, Draggable } from "react-beautiful-dnd";
import { pageDropAreaPrefix, pageDraggablePrefix } from "./OutlineSidebar";


interface chapterProps {
  chapter: ChapterSummary;
}


export const inClosingPageDropAreaPrefix : String = 'inClosingPageDropArea-';


export default function Chapter(props : chapterProps) {

  const [isPagesHidden, setIsPagesHidden] = useState(true);
  const innerPageList = useRef(null);
  const chapter = props.chapter;

  function toggleInnerPageList() {
    if (innerPageList.current) {
      // @ts-ignore
      innerPageList.current.classList.toggle('hidden');
      setIsPagesHidden(!isPagesHidden);
    }
  }




  return (
    <div>
      <Droppable droppableId={inClosingPageDropAreaPrefix + String(props.chapter.id)} isDropDisabled={!isPagesHidden} type='PAGE'>
        {(provided, snapshot) => (
          <div ref={provided.innerRef} {...provided.droppableProps}>
            <div onClick={toggleInnerPageList} className={(snapshot.isDraggingOver ? "bg-gray-700 " : "") + "flex items-center p-1 w-full text-base font-normal text-gray-900 rounded-lg transition duration-75 group hover:bg-gray-100 dark:text-white dark:hover:bg-gray-700"}>
              <svg aria-hidden="true" className="w-4 h-4 text-gray-800 dark:text-white" fill="none" viewBox="0 0 16 12"><path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M1 1h14M1 6h14M1 11h7"/></svg>
              <span className="flex-1 ml-3 text-left whitespace-nowrap">
                {chapter.title}
              </span>
              <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd"></path></svg>
            </div>
          </div>
        )}
      </Droppable>
      <ul className="hidden py-2 space-y-2" ref={innerPageList}>

        {/* 챕터 내부 페이지 드롭 영역 */}
        <Droppable droppableId={pageDropAreaPrefix + chapter.id} type='PAGE'>
          {(provided) => (
            <div ref={provided.innerRef} {...provided.droppableProps}>

              {/* Draggable Page 설정 */}
              {chapter.pages?.map((page, index) => (
                <Draggable key={pageDraggablePrefix + page.id} draggableId={pageDraggablePrefix + page.id} index={index}>
                  {(provided : any) => (
                    <div ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps}>

                      {/* Page 컴포넌트 */}
                      <Page page={page} />

                    </div>
                  )}
                </Draggable>
              ))}

              {provided.placeholder}

            </div>
          )}
        </Droppable>
        
      </ul>
    </div>
  );
}