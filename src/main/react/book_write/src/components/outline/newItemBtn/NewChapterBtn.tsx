import { create } from "zustand";
import { Outline } from "../../../types/types";


interface UseCreateChapterStore {
  isMutated : boolean;
  resetMutation: () => void;
  requestCreateChapter : () => void;
  resetPayload: () => void;
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
  },
  resetPayload: () => set(
    (state : any) => ({
      ...state,
    })
  )
}));


export default function NewChapterBtn({outline: localOutline} : {outline : Outline}) {

  const { requestCreateChapter } = useCreateChapterStore();

  return (
    <button type="button" onClick={() => requestCreateChapter()} className="absolute z-20 top-5 px-1 md:px-5 left-[7%] py-2 text-xs font-medium text-center text-gray-900 focus:outline-none bg-gray-200 rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-gray-700">
      새 챕터
    </button>
  );
}