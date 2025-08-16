import base64

# Substitua 'producao-818065-certificado-hallel.p12' pelo nome do seu arquivo, se for diferente
file_name = './producao-818065-certificado-hallel.p12'
output_file_name = './certificate_base64.txt'

try:
    with open(file_name, 'rb') as p12_file:
        p12_content = p12_file.read()

    base64_content = base64.b64encode(p12_content)

    with open(output_file_name, 'wb') as output_file:
        output_file.write(base64_content)

    print(f"Certificado convertido com sucesso! O conteúdo Base64 está em '{output_file_name}'")

except FileNotFoundError:
    print(f"Erro: O arquivo '{file_name}' não foi encontrado. Verifique o nome do arquivo e o diretório.")
except Exception as e:
    print(f"Ocorreu um erro: {e}")