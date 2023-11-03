'use restrict';

function OutlinePageWrapper(props){
    return (
        <li>
            <a href="#" class="flex p-1 items-center pl-6 w-full text-white text-sm font-normal text-gray-900 rounded-lg transition duration-75 group hover:bg-gray-700">
                {props.title}
            </a>
        </li>
    );
}

export default OutlinePageWrapper;