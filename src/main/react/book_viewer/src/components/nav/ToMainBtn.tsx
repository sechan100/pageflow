



export function ToMainBtn() {



  return (
    <>
      {/* eslint-disable-next-line no-restricted-globals */}
      <div onClick={() => { history.back()}} className="flex justify-start absolute left-[1vw] top-3">
        <div className="bg-gray-700 hover:bg-gray-900 w-12 h-12 p-3 mb-3 rounded-full cursor-pointer">
          <svg className="w-6 h-6 text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10">
            <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 5H1m0 0 4 4M1 5l4-4"/>
          </svg>
        </div>
      </div>  
    </>
  );
}