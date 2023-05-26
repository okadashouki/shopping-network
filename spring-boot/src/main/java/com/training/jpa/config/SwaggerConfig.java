package com.training.jpa.config;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.training.jpa.vo.GoodsDataCondition;
import com.training.jpa.vo.GoodsVo;
import com.training.jpa.vo.MemberInfo;
import com.training.jpa.vo.SalesReportCondition;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {  

   private static final String splitor = ";"; 

   public Docket createDocket(String groupName, ApiInfo apiInfo, String basePackage){
      return new Docket(DocumentationType.SWAGGER_2)
             .groupName(groupName)
             .apiInfo(apiInfo)
             .select()
             .apis(basePackage(basePackage))
             .paths(PathSelectors.any())
             .build();
   }

   @Bean
   public Docket createTrainingDocket() {
      return createDocket("TrainingSpringBootJpa", trainingApiInfo(), "com.training");
   }
   
   private ApiInfo trainingApiInfo() {
      return new ApiInfoBuilder()
             .licenseUrl("http://localhost:8085/training/swagger-ui/index.html")
             .title("Training API")
             .description("Training API 文件")
             .version("1.8")
             .build();
   }

   public static Predicate<RequestHandler> basePackage(final String basePackage) {
      return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
   }

   private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
      return input -> {
          for (String strPackage : basePackage.split(splitor)) {
             boolean isMatch = input.getPackage().getName().startsWith(strPackage);
             if (isMatch) {
                return true;
             }
          }
          return false;
      };
   }  

   @SuppressWarnings("deprecation")
private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
      return Optional.fromNullable(input.declaringClass());
   }  
   @Bean(name = "sessionCartGoods")
   public List<GoodsVo> cartGoods() {
       return new ArrayList<>();
   }
   @Bean
   public MemberInfo memberInfo() {
       MemberInfo memberInfo = new MemberInfo();
       // 设置 memberInfo 对象的属性
       return memberInfo;
   }
   @Bean
   public GoodsDataCondition goodsDataCondition() {
	   GoodsDataCondition goodsDataCondition= new GoodsDataCondition();
	   return goodsDataCondition;
   }
   @Bean
   public SalesReportCondition salesReportCondition() {
	   SalesReportCondition salesReportCondition= new SalesReportCondition();
	   return salesReportCondition;
   }
   @Bean
   public MemberInfo sessionMemberInfo() {
       return new MemberInfo();
   }
} 