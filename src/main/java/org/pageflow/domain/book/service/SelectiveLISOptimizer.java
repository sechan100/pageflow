package org.pageflow.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 오름차순이 파괴된 수열에서 불연속적인 항을 선택할 수 있는 최장 오름차순 부분배열을 구하는 알고리즘
 */
@Service
@RequiredArgsConstructor
public class SelectiveLISOptimizer {


    // 1, 2, 8, 3, 4, 100, 5, 6, 101, 102, 103, 104, 10, 11, 12, 13, 20, 22
    public List<Integer> findSelectiveLIS(List<Integer> sequence) {

        List<Integer> seq = new ArrayList<>(sequence);
        List<Integer> omitCandidates = new ArrayList<>();

        // 바로 뒤의 항이 더 작은 경우 즉, 해당 항이 오름차순을 파괴하는 항인 경우, 누락의 후보로 추가한다. omitCandidates에 있는 수들은 해당 수를 누락했을 경우의 IS를 계산한다.
        for (int i = 0; i < sequence.size() - 1; i++) {
            if (seq.get(i) > seq.get(i + 1)) omitCandidates.add(seq.get(i));
        }

        for (Integer omitTarget : omitCandidates) {
            Integer removeStartNum = isConstituentOfLIS(seq, seq, omitTarget);
            // 지워야하는 항이 있는 경우
            if (removeStartNum != -1) {
                // removeStartNum == omitTarget이라면 omitTarget만 지움
                if (removeStartNum.equals(omitTarget)) {
                    seq.remove(omitTarget);

                    // removeStartNum != omitTarget이라면 removeStartNum부터 omitTarget까지 지움
                } else {
                    for (Integer i = removeStartNum; i <= omitTarget; i++) {
                        seq.remove(i);
                    }
                }
            }
        }

        // 모든 누락 후보까지 검사를 완료하고 최종 LIS를 반환.
        return findIS(seq);
    }


    /**
     * 특정항을 제외하는 작업은 하지 않고, 하나하나 순서대로 조회하면서 오름차순이라면 채택, 아니라면 제하면서 IS를 찾는다.
     *
     * @param sequence IS를 추출할 수열
     * @return sequence의 IS
     */
    private List<Integer> findIS(List<Integer> sequence) {

        List<Integer> IS = new ArrayList<>();

        for (int i = 0; i < sequence.size(); i++) {
            Integer present = sequence.get(i);
            Integer prev = i != 0 ? IS.get(IS.size() - 1) : null;

            // 첫 항이거나, 이전 항보다 큰 경우
            if (i == 0 || present > prev) {
                IS.add(sequence.get(i));
            }
        }
        return IS;
    }

    /**
     * @param baseSequence IS를 구할 수열
     * @param omitTarget   누락할 대상
     * @return -1: 해당 항을 누락해서는 안됨. / Integer: 반환값 ~ omitTarget까지의 배열을 지워도 됨. 반환값과 omitTarget이 같다면, omitTarget의 값만을 지우라는 의미.
     */
    private Integer isConstituentOfLIS(List<Integer> baseSequence, List<Integer> beforeCalledOmitTargetRemovedSequence, Integer omitTarget) {

        List<Integer> beforeCalledOmitTargetRemovedSeq = new ArrayList<>(beforeCalledOmitTargetRemovedSequence);

        // 그냥 IS를 구한다.
        List<Integer> IS = findIS(baseSequence);

        // 제거할 대상 바로 앞에있는 수의 인덱스. 만약 0이라면 배열 맨 앞까지 제거했을 때에도 IS가 줄어들기만 했다는 것으로, 즉 모두 제거하지 않는 것이 LIS라는 의미다.
        int prevIntOmitTargetNum = baseSequence.indexOf(omitTarget) - 1;
        if (prevIntOmitTargetNum == 0 && baseSequence.size() != beforeCalledOmitTargetRemovedSequence.size()) return -1;

        // omitTarget을 누락하고 IS를 구한다.
        boolean isOmited = beforeCalledOmitTargetRemovedSeq.remove(omitTarget);
        List<Integer> afterOmitedIS = findIS(beforeCalledOmitTargetRemovedSeq);


        // 만약, 누락하고 계산한 IS가 더 길거나 같은 길이면, omitTarget을 리턴 -> omitTarget을 지우라.
        if (IS.size() <= afterOmitedIS.size()) {
            return omitTarget;

            // 누락하고 계산한 결과가 더 짧은 경우, 아직은 기여를 판단할 수 없음.
        } else {
            // 앞에 놈을 계속 지워가면서 계산해본다.
            return isConstituentOfLIS(baseSequence, beforeCalledOmitTargetRemovedSeq, baseSequence.get(prevIntOmitTargetNum));
        }
    }
}
