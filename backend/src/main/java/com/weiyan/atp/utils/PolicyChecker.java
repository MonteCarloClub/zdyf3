package com.weiyan.atp.utils;

import com.weiyan.atp.data.bean.Result;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author BerryChen0w0
 * @date 2024-07-28 23:41
 */
public class PolicyChecker {

//    private static String AttrPattern = "[a-zA-Z0-9\u4e00-\u9f5a]+:[a-zA-Z0-9\u4e00-\u9fa5]+";

    public static Result<Object> policyIsValid(String policy) {
        String AttrPattern = "[a-zA-Z0-9\u4e00-\u9f5a]+:[a-zA-Z0-9\u4e00-\u9fa5]+";
        String str = policy;
        // 检查str是否合法
        // 思路是，首先提取全部是AttrPattern，去重，然后看看有几个，如果只有1个，报错
        // 如果有多个，那么把str中的AttrPattern全部替换成A
        // 然后，用栈处理：遇到空格就跳过；遇到A或'('就入栈，遇到')'就进行：
        //      top == A ? pop : return false;
        //      top == [AND|OR] ? pop : return false;
        //      top == A ? pop : return false;
        //      top == '(' ? pop : return false;
        // 最终，str全部扫描一遍后，栈中应该是空的。如果是，return true; 否则false

        // 1.提取全部的AttrPattern，检查是否>=2个且不重复
        System.out.println("[br]in ShareContentRequest.policyIsValid(): AttrPattern = " + AttrPattern);
        Matcher matcher = Pattern.compile(AttrPattern).matcher(str);
        Set<String> attrSet = new HashSet<>();
        while (matcher.find()) {
            String attr = matcher.group();
            if (attrSet.contains(attr)) {
//                System.out.println("同一属性不能多次出现：" + attr);
                return Result.internalError("同一属性不能多次出现：" + attr);
            }
            attrSet.add(attr);
        }
        if (attrSet.size() <= 1) {
//            System.out.println("策略表达式格式错误：至少要有两个属性(<username>:<attr_name>)");
            return Result.internalError("策略表达式格式错误：至少要有两个属性(<username>:<attr_name>)");
        }
        // 2.把所有AttrPattern替换成"."，用"."表示原本的一个属性
        if (str.contains(".")) {
            // 先检查，如果str里本身包含这个"."，那直接报错
//            System.out.println("策略表达式格式错误");
            return Result.internalError("策略表达式格式错误");
        }
        str = matcher.replaceAll(" . ");

        // 3.把AND,OR替换成"*"
        Matcher matcher2 = Pattern.compile("\\s((AND)|(OR))\\s").matcher(str);
        if (str.contains("*")) {
            // 先检查，如果str里本身包含这个"*"，那直接报错
//            System.out.println("策略表达式格式错误");
            return Result.internalError("策略表达式格式错误");
        }
        str = matcher2.replaceAll(" * ");
        // 另外，附一个提示吧（AND和OR大写问题）
        Matcher matcher3 = Pattern.compile("\\s((and)|(or))\\s").matcher(str);
        if (matcher3.find()) {
//            System.out.println("策略表达式格式错误：AND和OR要大写");
            return Result.internalError("策略表达式格式错误：AND和OR要大写");
        }
        // 4.用栈处理，检查括号和AND,OR等情况
        // 经过上面的处理，此时str应该是((. * .) * .)类似的形状了
        System.out.println("[br]in ShareContentRequest.policyIsValid(): str:"+str);
        Stack<Character> stack = new Stack<>();
        try {
            for (char ch : str.toCharArray()) {
                if (ch == ' ') {
                    continue;
                }
                if (ch == '(' || ch == '.' || ch == '*') {
                    stack.push(ch);
                } else if (ch == ')') {
                    if (stack.pop() != '.' || stack.pop() != '*' || stack.pop() != '.' || stack.pop() != '(') {
//                        System.out.println("策略表达式格式错误");
                        return Result.internalError("策略表达式格式错误");
                    }
                    if (!stack.isEmpty()) {
                        stack.push('.');
                    }
                } else {
                    // ch是其他字符，表明策略表达式错误
//                    System.out.println("策略表达式格式错误");
                    return Result.internalError("策略表达式格式错误");
                }
            }
            if (!stack.isEmpty()) {
//                System.out.println("策略表达式格式错误");
                return Result.internalError("策略表达式格式错误");
            }
        } catch (EmptyStackException e) {
            // 空栈的exception，说明策略表达式有错误。否则正确的表达式格式是不会出现空栈情况的
//            System.out.println("策略表达式格式错误");
            return Result.internalError("策略表达式格式错误");
        }

        return Result.success();
    }
}
