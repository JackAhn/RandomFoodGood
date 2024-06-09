package com.jackahn.randomfoodgood.view.main.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.jackahn.randomfoodgood.R
import com.jackahn.randomfoodgood.dao.User
import com.jackahn.randomfoodgood.databinding.FragmentMypageBinding
import com.jackahn.randomfoodgood.util.RetrofitUtil
import com.jackahn.randomfoodgood.util.SNSUtil
import com.jackahn.randomfoodgood.view.login.LoginActivity
import com.jackahn.randomfoodgood.view.main.MainActivity
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = (requireActivity() as MainActivity).getUser()

        binding.myPageNameText.text = user.userName + "님의 마이페이지"
        binding.myPageEmailText.text = user.userId

        Log.i("mypage-socialid", "SocialId : " + user.socialId)

        // 로그아웃
        binding.myPageLogOutBtn.setOnClickListener {
            var isSuccess = false
            if(user.socialId == 1){
                //구글
                SNSUtil.googleSignInClient.signOut()
                    .addOnSuccessListener {
                        goMain("로그아웃이 완료되었습니다.")
                    }
            }
            else if(user.socialId == 2) {
                //카카오
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Toast.makeText(requireActivity(), "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                    }else {
                        goMain("로그아웃이 완료되었습니다.")
                    }
                }
            }
            else if(user.socialId == 3){
                //네이버
                NaverIdLoginSDK.logout()
                goMain("로그아웃이 완료되었습니다.")
            }
        }

        // 회원탈퇴
        binding.myPageExitBtn.setOnClickListener {
            if(user.socialId == 1){
                //구글
                SNSUtil.googleSignInClient.revokeAccess()
                    .addOnCompleteListener {
                        requestDelete()
                    }
            }
            else if(user.socialId == 2) {
                //카카오
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        Toast.makeText(requireActivity(), "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                    }else {
                        requestDelete()
                    }
                }
            }
            else if(user.socialId == 3){
                //네이버
                NaverIdLoginSDK.getAccessToken()
                NidOAuthLogin().callDeleteTokenApi(object: OAuthLoginCallback {
                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        Log.d("Delete-Naver", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                        Log.d("Delete-Naver", "errorCode: ${NaverIdLoginSDK.getLastErrorDescription()}")
                    }

                    override fun onSuccess() {
                        requestDelete()
                    }
                })
            }
        }
    }

    private fun requestDelete(){
        RetrofitUtil.userUtil.deleteUser(user.id!!.toLong()).enqueue(object: Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if(response.body()!! == 1){
                    goMain("회원탈퇴가 완료되었습니다.")
                }
                else{
                    Toast.makeText(requireContext(), "서버 에러 발생", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun goMain(text: String){
        Toast.makeText(requireActivity(), text, Toast.LENGTH_SHORT).show()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}