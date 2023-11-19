/* eslint-disable @typescript-eslint/no-unused-vars */
import axios from 'axios';
import { ChapterMutation, Outline } from '../types/types';
import { UseMutateAsyncFunction, useMutation } from 'react-query';
import { QueryContext } from '../App';
import flowAlert from '../etc/flowAlert';
import { useContext } from 'react';




export const useCreateChapterMutation = (bookId : number) : { mutateAsync: UseMutateAsyncFunction<void, unknown, void, unknown>, isLoading: boolean, isError: boolean} => {

  const {queryClient} = useContext(QueryContext);

  const { mutateAsync, isLoading, isError } = useMutation(
    async () => {

      const response = await axios.post(`/api/book/${bookId}/chapter`);
      
      if(response.status !== 200){
        throw new Error("챕터를 생성하는데 실패했습니다.");
      }

      if(response.data){
        console.log("Sercer Response: create new chapter", response.data);
      }
    },
    {
      onSuccess: () => {
        // 임시캐시 설정
        queryClient.invalidateQueries(['book', bookId]);
      }
    }
  )

  return {mutateAsync, isLoading, isError};
}




// chapter 데이터 업데이트 훅
export const useChapterMutation = () : { mutateAsync: UseMutateAsyncFunction<void, unknown, ChapterMutation[], unknown>, isLoading: boolean, isError: boolean } => {

  const {queryClient, bookId} = useContext(QueryContext);

  const { mutateAsync, isLoading, isError } = useMutation(
    async (chapterMutations : ChapterMutation[]) => {


      const response = await axios.put(`/api/book/${bookId}/chapters`, chapterMutations, {
        headers: {
          'Content-Type': 'application/json'
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

  return { mutateAsync, isLoading, isError };


}