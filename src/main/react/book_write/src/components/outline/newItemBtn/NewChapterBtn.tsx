import { create } from "zustand";
import { Outline } from "../../../types/types";


interface UseCreateChapterStore {
  isMutated : boolean;
  resetMutation: () => void;
  requestCreateChapter : () => void;
}

export const useCreateChapterStore = create<UseCreateChapterStore>((set : any) => ({
  isMutated : false,
  resetMutation: () => set(
    (state : any) => ({
      ...state,
      isMutated: false
    })
  ),
  requestCreateChapter : () => {
    set((state : any) => ({
      isMutated: true
    }));
  }
}));


export default function NewChapterBtn({outline: localOutline} : {outline : Outline}) {

  const { requestCreateChapter } = useCreateChapterStore();

  return (
    <button type="button" onClick={() => requestCreateChapter()} className="fixed z-20 top-4 left-40 px-5 py-2 text-xs font-medium text-center ext-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700">새 챕터</button>
  );
}