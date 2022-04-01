package com.ybouidjeri.newsapi

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import org.junit.runner.RunWith

import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

    @get:Rule
    val exceptionRule = ExpectedException.none()


    @Test
    fun test_MD5() {
        var input = "http://mywebsite.com"
        var expected = "d91a39c7fa5e59e2d2461afdb370be47"
        var actual = Utils.md5(input)
        Assert.assertEquals(expected, actual)

        input = ""
        expected = "d41d8cd98f00b204e9800998ecf8427e"
        actual = Utils.md5(input)
        Assert.assertEquals(expected, actual)
    }

}