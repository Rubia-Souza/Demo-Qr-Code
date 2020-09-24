package io.github.rz.demo.utils.codigos.impl.qr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Hashtable;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import io.github.rz.demo.excecoes.GeracaoCodigoException;
import io.github.rz.demo.utils.codigos.IGeradorCodigo;

public class GeradorCodigoQr<T> implements IGeradorCodigo<T> {

	private final String FORMATACAO_PADRAO_QR_CODE = "ISO-8859-1";
	private final String TAMANHO_MARGEM_QR_CODE = "3";
	
	private final String MSG_ERRO_GERACAO_CODIGO_QR = "Erro ao gerar o código Qr: %s";
	private final String MSG_ERRO_CONVERSAO_CODIGO_QR = "Erro ao converter o código Qr para Base64: %s";
	
	@Override
	public String criarCodigoBase64(T dados, Integer altura, Integer largura, String formato) {
		Hashtable<EncodeHintType, String> parametrosWriter = montarConfiguracaoWriter();
		String conteudo = converterDados(dados);
		
		BitMatrix matrizCodigoGerada;
		try {
			QRCodeWriter writer = new QRCodeWriter();
			matrizCodigoGerada = writer.encode(conteudo, BarcodeFormat.QR_CODE, largura, altura, parametrosWriter);
		} catch (WriterException ex) {
			throw new GeracaoCodigoException(String.format(MSG_ERRO_GERACAO_CODIGO_QR, ex.getMessage()));
		}
		
		String codigoQrBase64 = converterMatrizBitsImagem(matrizCodigoGerada, formato);
		return codigoQrBase64;
	}
	
	private Hashtable<EncodeHintType, String> montarConfiguracaoWriter() {
		Hashtable<EncodeHintType, String> parametrosExtras = new Hashtable<>();
		parametrosExtras.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H.toString());
		parametrosExtras.put(EncodeHintType.CHARACTER_SET, FORMATACAO_PADRAO_QR_CODE);
		parametrosExtras.put(EncodeHintType.MARGIN, TAMANHO_MARGEM_QR_CODE);
		
		return parametrosExtras;
	}
	
	private String converterDados(T dados) {
		Gson conversorJson = new Gson();
		return conversorJson.toJson(dados);
	}
	
	private String converterMatrizBitsImagem(BitMatrix matriz, String formatoImagem) {
		ByteArrayOutputStream streamImagemBytes = new ByteArrayOutputStream();   
		
		try {
			MatrixToImageWriter.writeToStream(matriz, formatoImagem, streamImagemBytes);
			streamImagemBytes.close();
		} catch (IOException ex) {
			throw new GeracaoCodigoException(String.format(MSG_ERRO_CONVERSAO_CODIGO_QR, ex.getMessage()));
		}
		
		String imagem = Base64.getEncoder().encodeToString(streamImagemBytes.toByteArray());
		return imagem;
	}
}