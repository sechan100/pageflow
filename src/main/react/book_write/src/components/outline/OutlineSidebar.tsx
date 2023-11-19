import { Outline } from "../../types/types";
import NewChapterBtn from "./newItemBtn/NewChapterBtn";


export interface IOutlineSidebarProps {
  children: React.ReactNode;
  outline: Outline;
}


export default function OutlineSidebar({children, outline: localOutline} : IOutlineSidebarProps) {


  return (
    <div className='flex'>
      <aside id="page-outline-sidebar" className="fixed z-10 top-0 left-0 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0">
        <div className="overflow-y-auto pt-12 pb-5 px-3 h-full bg-white border-r border-gray-200 dark:bg-gray-800 dark:border-gray-700">
          {children}
        </div>
        <NewChapterBtn outline={localOutline} />
      </aside>
      <div id="sidebar-placeholder" className="w-64 h-screen hidden sm:block"></div>
    </div>
  );
}
