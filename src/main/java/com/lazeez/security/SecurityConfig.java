    package com.lazeez.security;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import javax.servlet.http.HttpServletResponse;


    @EnableWebSecurity
    @Configuration
    public class SecurityConfig extends WebSecurityConfigurerAdapter {


        @Autowired
        JwtRequestFilter jwtRequestFilter;
        @Autowired
        UserDetailService userDetailsService;
        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }






        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }




        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and()
                    .authorizeRequests()
                    .antMatchers("/user/addToCart/**","/user/getCart/"
                            ,"/order/getOrderByUserId/**","/order/placeOrder"


                    ).hasAuthority("USER")

                    .antMatchers("/user/getAllUser","/order/getAllOrder","/product/addProduct","/product/updateProduct/**","product/deleteProduct/**").hasAuthority("ADMIN")
                    .antMatchers("/user/updateUser/","order/deletedOrder/**").hasAnyAuthority("ADMIN","USER")
                    .antMatchers("/user/deleteUser/","/user/getUser/","/user/getUserData","/user/incrCartItem/*","user/decCartItem/*").authenticated()
                    .anyRequest().permitAll()


                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(
                            (request, response, ex) -> {
                                response.sendError(
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        ex.getMessage()
                                );
                            }
                    )
                    .and().csrf().disable()

                    .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);








        }


        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return  new BCryptPasswordEncoder();
        }


    //    @Bean
    //    public FilterRegistrationBean  coresFilter()
    //    {
    //       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //        CorsConfiguration corsConfiguration = new CorsConfiguration();
    //
    //        corsConfiguration.setAllowCredentials(true);
    //        corsConfiguration.addAllowedOriginPattern("*");
    //        corsConfiguration.addAllowedHeader("Authorization");
    //        corsConfiguration.addAllowedHeader("Content-Type");
    //        corsConfiguration.addAllowedMethod("POST");
    //        corsConfiguration.addAllowedMethod("GET");
    //        corsConfiguration.addAllowedMethod("DELETE");
    //        corsConfiguration.addAllowedMethod("PUT");
    //        corsConfiguration.addAllowedMethod("OPTIONS");
    //        corsConfiguration.setMaxAge(3600L);
    //
    //        source.registerCorsConfiguration("/**",corsConfiguration);
    //        FilterRegistrationBean  bean = new FilterRegistrationBean(new CorsFilter(source));
    //
    //        return  bean;
    //
    //
    //    }

    }
