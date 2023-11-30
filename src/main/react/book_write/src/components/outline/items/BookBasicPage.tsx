import { Link } from 'react-router-dom';
import { Outline } from '../../../types/types';
import { useBookMutationStore } from '../../form/pages/book/BookForm';




export default function BookBasicPage({outline : localOutline} : {outline : Outline}){

  const bookStore = useBookMutationStore();

  return (
    <div>
      <Link to="/" replace className="flex border mb-2 items-center p-2 text-base font-normal text-white rounded-lg bg-gray-800 hover:bg-gray-700 group">
        <svg className="w-6 h-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 16 20">
          <path d="M16 14V2a2 2 0 0 0-2-2H2a2 2 0 0 0-2 2v15a3 3 0 0 0 3 3h12a1 1 0 0 0 0-2h-1v-2a2 2 0 0 0 2-2ZM4 2h2v12H4V2Zm8 16H3a1 1 0 0 1 0-2h9v2Z"/>
        </svg>
        <span className="ml-3 text-lg truncate">{bookStore.payload.title ? bookStore.payload.title : localOutline.title}</span>
      </Link>
    </div>
  );
}