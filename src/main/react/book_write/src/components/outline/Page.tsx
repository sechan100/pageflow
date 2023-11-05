import React from 'react';
import {IPage} from '../../types/book'

interface pageProps {
  page: IPage;
}

export default function Page(props : pageProps){
  return (
    <li>
      <a href={"/" + props.page.id} className="flex p-1 items-center pl-6 w-full text-white text-sm font-normal text-gray-900 rounded-lg transition duration-75 group hover:bg-gray-700">
        {props.page.title}
      </a>
    </li> 
  );
}