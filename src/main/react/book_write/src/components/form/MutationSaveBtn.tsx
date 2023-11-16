/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect, useRef } from "react";


interface MutationSaveBtnProps {
  setSaveActive : React.Dispatch<React.SetStateAction<boolean>>;
  isUpdated : React.MutableRefObject<boolean>;
}



export default function MutationSaveBtn({setSaveActive, isUpdated} : MutationSaveBtnProps){

  const updateAlertTooltip = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if(isUpdated.current){
      updateAlertPingHandler();
    }
  }, [isUpdated.current]);

  function saveActiveHandler(){
    if(isUpdated.current){
      setSaveActive(
        (prevSaveActive) => !prevSaveActive
      );
    }
  }


  return (
    <div onClick={saveActiveHandler} className="flex justify-start fixed z-50 right-7 top-7">
      {isUpdated.current && 
      <div className="relative flex items-center mb-2 mr-3 transition-opacity duration-[1500ms] opacity-0" ref={updateAlertTooltip}>
        <div className="tooltip bg-white text-black border border-gray-300 py-1 px-2 rounded shadow-lg">
          변경사항이 있습니다
          <div className="tooltip-arrow absolute top-[40%] right-1 w-0 h-0 border-transparent border-solid border-l-2 border-t-2 border-b-2 transform -translate-y-1/2 -translate-x-1/2"></div>
        </div>
      </div>}
      <div className={ (isUpdated.current ? "bg-gray-700 hover:bg-gray-900" : "bg-gray-500") + " w-12 h-12 p-3 mb-3 rounded-full cursor-pointer"}>
      {isUpdated.current && 
        <span className="absolute top-1 right-[1px] flex h-3 w-3">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-sky-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-3 w-3 bg-sky-500"></span>
        </span>}
        <svg className="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 16 18">
          <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M8 1v11m0 0 4-4m-4 4L4 8m11 4v3a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2v-3"/>
        </svg>
      </div>
    </div>
  );


  function updateAlertPingHandler(){
    setTimeout(() => {
      if (updateAlertTooltip.current) {
        updateAlertTooltip.current.classList.remove("opacity-0");
      }
    }, 100);
  }
}