import numpy as np
from nltk import word_tokenize
from nltk.corpus import stopwords
from gensim import models
import scipy.spatial.distance as scp

STOPWORDS = stopwords.words('german')

class TextIterator:
    def __init__(self,corpus):
        self.text_list = self.init_text_list(corpus)

    def init_text_list(self, corpus):
        text_list = []
        if type(corpus) == type({}):
            for id in list(corpus):
                text_list.append(corpus[id].lower())
        elif type(corpus) == type([]):
            for id, text in corpus:
                text_list.append(text.lower())
        return text_list

    def __iter__(self):     #iterator über die wörter im text
        for full_text in self.text_list:
            yield word_tokenize(full_text)

class Document:
    def __init__(self, id, text, embeddings, dim = 200):
        self.id = id
        self.text = text
        self.embedding, words_not_found = embed_document(self.text, embeddings, dim = dim)

    def get_id(self):
        return self.id

    def get_text(self):
        return self.text

    def get_embedding(self):
        return self.embedding

class Corpus:
    def __init__(self, corpus, embedding_path, dim = 200):
        #input corpus ist entweder liste [(id, doc_text),...] oder dict {id:doc_text}
        self.embeddings = self.train_word_embeddings(embedding_path, TextIterator(corpus), dim = dim)
        self.corpus_dict = self.import_corpus(corpus)
        self.embedding_matrix = [] #todo implement fast similarity computation via matrix
        self.dim = dim

    def import_corpus(self, corpus):
        dict = {}
        if type(corpus) == type({}):
            for id in list(corpus):
                dict[id] = Document(id, corpus[id], dim = self.dim)
        elif type(corpus) == type([]):
            for id, text in corpus:
                dict[id] = Document(id, text)
        return dict

    def train_word_embeddings(self, path, text_iterator, dim = 200, min_word_count = 5):
        model = models.Word2Vec(text_iterator, size=dim, min_count=min_word_count, workers=2)
        model.save(path + "/embeddings.txt")
        return model

    def fast_similarity(self, new_document, add_new_doc = False, top_n = 10):
        id, text = new_document     #todo: in welchem Format kommen neue Dokumente allgemein an?
        if add_new_doc:
            self.embeddings.train([word.lower() for word in word_tokenize(text)])
            self.corpus_dict[id] = text

        document_similarities = [] #[(id,similarity),...]
        embedded_document = embed_document(text, self.embeddings, self.dim)

        #TODO: implement fast cosine similarity computation
        for id in list(self.corpus_dict):
            document_similarities.append((id, cosine_similarity(embedded_document, self.corpus_dict[id].get_embedding())))

        document_similarities = sorted(document_similarities, key = lambda x : x[1], reversed = True)
        if top_n == -1:
            return document_similarities
        else:
            return document_similarities[:top_n]




def cosine_similarity(vec1,vec2):
    return 1 - scp.cosine(vec1,vec2)

def embed_document(text, embeddings, dim = 200):
    result = np.zeros((dim,))
    words_not_found = []
    words = [word.lower() for word in word_tokenize(text) if word.lower() not in stopwords]
    for word in words:
        try:
            result += embeddings[word]
        except KeyError:
            words_not_found.append(word)
    return result, words_not_found


