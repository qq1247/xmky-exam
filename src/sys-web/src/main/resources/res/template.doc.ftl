<#list list as question>
【${question.typeName}】【${question.difficultyName}】${question.title}
<#if question.optionA != "AAAAAAAAAA">
A.${question.optionA}
</#if>
<#if question.optionB != "BBBBBBBBBB">
B.${question.optionB}
</#if>
<#if question.optionC != "CCCCCCCCCC">
C.${question.optionC}
</#if>
<#if question.optionD != "DDDDDDDDDD">
D.${question.optionD}
</#if>
<#if question.optionE != "EEEEEEEEEE">
E.${question.optionE}
</#if>
<#if question.optionF != "FFFFFFFFFF">
F.${question.optionF}
</#if>
<#if question.optionG != "GGGGGGGGGG">
G.${question.optionG}
</#if>
【答案】${question.answer}
【分值】${question.score}
【分值选项】${question.scoreOptions}
【解析】${question.analysis}
</#list>