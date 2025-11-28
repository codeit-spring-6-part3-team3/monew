package com.team03.monew.interest.util;

public class SimilarityCheck {

    public static boolean isSimilar(String tinsName, String targetName) {

        //둘 중 더 긴 문자열 길이 구하기
        //길이가 긴 기준으로 유사도 검사
        int maxLength = Math.max(tinsName.length(), targetName.length());

        //레벤슈타인 거리 계산
        int distance = getLevenshteinDistance(tinsName,targetName);

        // 유사도 검사
        double similarity = 1.0 -((double) distance / maxLength);

        return similarity >= 0.8;

    }

    //레벤슈타인 거리 계산 알고리즘
    private static int getLevenshteinDistance(String s1, String s2){
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {

                // 두 글자가 같으면 비용 0, 다르면 1
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;

                dp[i][j] = Math.min(Math.min(dp[i-1][j] + 1, dp[i][j-1]),dp[i-1][j-1] + cost);
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
