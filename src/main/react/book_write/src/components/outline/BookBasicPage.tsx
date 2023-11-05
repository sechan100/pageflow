import { useGetBook } from '../../api/book-apis';
import {IBook} from '../../types/book'


interface IProps {
  bookId : number
}

export default function BookBasicPage({bookId} : IProps){

  const {data, isLoading, isFetching, error} = useGetBook(bookId);
  const book = data;

  return (
    <div>
      <a href="#" className="flex items-center p-2 text-base font-normal text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
        <svg className="w-6 h-6 text-gray-800 dark:text-white" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 16 20">
          <path d="M16 14V2a2 2 0 0 0-2-2H2a2 2 0 0 0-2 2v15a3 3 0 0 0 3 3h12a1 1 0 0 0 0-2h-1v-2a2 2 0 0 0 2-2ZM4 2h2v12H4V2Zm8 16H3a1 1 0 0 1 0-2h9v2Z"/>
        </svg>
        <span className="ml-3 text-lg">{book?.title}</span>
      </a>
    </div>
  );
}