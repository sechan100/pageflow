




interface ProgressionBarProps {
  progressPercentage: number;

}


export default function ProgressionBar({progressPercentage} : ProgressionBarProps) {


  return(
    <div className="py-3">

      <div className="w-full bg-gray-300 rounded-full">
        <div className="bg-blue-600 text-xs font-medium text-blue-100 text-center p-0.5 leading-none rounded-full" style={{width: `${progressPercentage}%`}}>{progressPercentage}%</div>
      </div>

    </div>
  );
}