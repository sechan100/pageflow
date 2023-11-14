



interface formSidebarProps {
  bookId : number;
  queryClient : any;
}


export default function FormSidebar(props : formSidebarProps){


  return (
    <div className="absolute z-50 left-10 top-10">
      <ul className="flex justify-between">
        {/* <BallMenuItem></BallMenuItem> */}
        <li>메뉴 2</li>
        <li>메뉴 3</li>
        <li>메뉴 4</li>
      </ul>
    </div>
  );
}



// function BallMenuItem