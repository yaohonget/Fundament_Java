package com.hong.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.DatatypeConverter;

/**
 * This is the sample code for encrypting message fields with the RSA key for
 * QuickGateway.
 */
public class MessageFieldEncryptor
{
    private static final Pattern theKeyIdSubjectPattern =
        Pattern.compile( ".*CN=Message Encryption Key (K\\d{1,3}).*" );
    private X509Certificate myCertificate;
    private final String myKeyId;

    public MessageFieldEncryptor( final File certificateFile )
            throws IOException, CertificateException
    {
        final InputStream certIn = new FileInputStream( certificateFile );
        try
        {
            myCertificate =
                (X509Certificate)CertificateFactory.getInstance( "X.509" )
                    .generateCertificate( certIn );
        }
        finally
        {
            certIn.close();
        }
        myKeyId = extractKeyId( myCertificate.getSubjectX500Principal() );
    }

    public MessageFieldEncryptor( final X509Certificate certificate )
    {
        myCertificate = certificate;
        myKeyId = extractKeyId( myCertificate.getSubjectX500Principal() );
    }

    private String extractKeyId( final X500Principal subjectX500Principal )
    {
        final String subject = subjectX500Principal.getName();
        final Matcher matcher = theKeyIdSubjectPattern.matcher( subject );
        if ( !matcher.matches() )
        {
            throw new IllegalStateException(
                "Could not get key identifier from subject " + subject );
        }
        return matcher.group( 1 );
    }

    public String encryptField( final String value )
            throws NoSuchAlgorithmException,
                   NoSuchProviderException,
                   NoSuchPaddingException,
                   InvalidKeyException,
                   IllegalBlockSizeException,
                   BadPaddingException
    {
        final Cipher rsa = Cipher.getInstance( "RSA", "SunJCE" );
        rsa.init( Cipher.ENCRYPT_MODE, myCertificate );
        return "{" + myKeyId + "}" +
            DatatypeConverter.printHexBinary( rsa.doFinal( value.getBytes() ) );
    }

    public static void main( final String[] args ) throws Exception
    {
        final MessageFieldEncryptor encryptor =
            new MessageFieldEncryptor( new File( "MessageEncryptionKey.cer" ) );

        final String cardNumber = "4444333322221111";

        System.out.println( encryptor.encryptField( cardNumber ) );
    }
}