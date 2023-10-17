package com.wenqi.book.geektimebasemath;

import java.util.Arrays;

/**
 * 归并排序
 *
 * 
 * @author liangwenqi
 * @date 2023/10/17
 */
public class Lesson6_1 {

    public static void main(String[] args) {
        //int[] result = merge_sort(new int[]{7, 6, 2, 4, 1, 9, 3, 8, 0, 5});
        int[] result = merge_sort(new int[]{3434, 3356, 67, 12334, 878667, 387});
        System.out.println("result: " + Arrays.toString(result));
    }

    public static int[] merge_sort(int[] to_sort) {
        int length = to_sort.length;

        if (length == 1) {
            return to_sort;
        }

        int mid = length / 2;
        int[] left = Arrays.copyOfRange(to_sort, 0, mid);
        int[] right = Arrays.copyOfRange(to_sort, mid, length);


        left = merge_sort(left);
        right = merge_sort(right);

        // 合并
        int maxLength = left.length + right.length;
        int[] merge = new int[maxLength];
        int lIndex = 0;
        int rIndex = 0;
        for (int i = 0; i < maxLength; i++) {
            int temp = Integer.MAX_VALUE;
            int temp2 = Integer.MAX_VALUE;
            if (lIndex < left.length) {
                temp = left[lIndex];
            }

            if (rIndex < right.length) {
                temp2 = right[rIndex];
            }

            if (temp > temp2) {
                merge[i] = temp2;
                rIndex++;
            } else {
                merge[i] = temp;
                lIndex++;
            }
        }

        return merge;
    }
}
