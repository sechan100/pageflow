import React from 'react';



interface MutationSaveBtnProps {
  setSaveActive : React.Dispatch<React.SetStateAction<boolean>>;
  isUpdated : React.MutableRefObject<boolean>;
}



export default function MutationSaveBtn({setSaveActive, isUpdated} : MutationSaveBtnProps){



  function saveActiveHandler(){
    if(isUpdated.current){
      setSaveActive(
        (prevSaveActive) => !prevSaveActive
      );
    }
  }


  return (
    <div onClick={saveActiveHandler} className="flex justify-start fixed z-50 right-7 top-7">
      <div className={ (isUpdated.current ? "bg-gray-700 hover:bg-gray-900" : "bg-gray-500") + " w-12 h-12 p-3 mb-3 rounded-full cursor-pointer"}>
        <svg className="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 16 18">
          <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 1v11m0 0 4-4m-4 4L4 8m11 4v3a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2v-3"/>
        </svg>
      </div>
    </div>
  );
}