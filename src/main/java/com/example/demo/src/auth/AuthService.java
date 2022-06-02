package com.example.demo.src.auth;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.auth.model.*;
//import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
//import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class AuthService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final JwtService jwtService;


    @Autowired
    public AuthService(AuthDao authDao, AuthProvider authProvider, JwtService jwtService) {
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtService = jwtService;

    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        User user = authDao.getPwd(postLoginReq);
        String encryptPwd;

        try {
            encryptPwd = new SHA256().encrypt(postLoginReq.getPwd());
        } catch (Exception e) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        if (user.getPwd().equals(encryptPwd)) {
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);

            return new PostLoginRes(userIdx, jwt);
        }
        else
            throw new BaseException(FAILED_TO_LOGIN);
    }




}
