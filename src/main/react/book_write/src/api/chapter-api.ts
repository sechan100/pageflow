/* eslint-disable @typescript-eslint/no-unused-vars */
import axios from 'axios';
import { BookMutation, ChapterMutation, Outline } from '../types/types';
import { UseMutateAsyncFunction, useMutation } from 'react-query';
import { QueryContext } from '../App';
import flowAlert from '../etc/flowAlert';
import { useContext } from 'react';



// Book 데이터 업데이트 훅
export const useChapterMutation = (chapterId : number) : [UseMutateAsyncFunction<any, unknown, ChapterMutation, unknown>, boolean, boolean] => {

  const {queryClient, bookId} = useContext(QueryContext);

  const { mutateAsync, isLoading, isError } = useMutation(
    async (chapterUpdateRequest : ChapterMutation) => {

      const formDate = new FormData();
      if(chapterUpdateRequest.title) formDate.append("title", chapterUpdateRequest.title);


      const response = await axios.put(`/api/book/${bookId}/chapter/${chapterId}`, formDate, {
        headers: {
          'Content-Type': 'multipart/form-data' // 확장 가능성 염두
        }
      });
      
      if(response.status !== 200){
        flowAlert("error", "챕터 정보를 업데이트하는데 실패했습니다.");
        throw new Error("챕터 정보를 업데이트하는데 실패했습니다.");
      }

      if(response.data){
        console.log("Chapter Update Success!", response.data);
      }
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['book', bookId]);
      }
    }
  )

  return [mutateAsync, isLoading, isError];


}